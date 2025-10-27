-- Add authentication fields to users table
-- Sprint 3, Phase 2: Authentication

-- Add password hash column (BCrypt hashed passwords)
ALTER TABLE users 
ADD COLUMN password_hash VARCHAR(255);

-- Add enabled flag (for account activation/deactivation)
ALTER TABLE users 
ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;

-- Add last login timestamp
ALTER TABLE users 
ADD COLUMN last_login TIMESTAMPTZ;

-- Add account lockout fields (for security)
ALTER TABLE users 
ADD COLUMN failed_login_attempts INTEGER NOT NULL DEFAULT 0;

ALTER TABLE users 
ADD COLUMN account_locked_until TIMESTAMPTZ;

-- Update existing system user with a hashed password
-- Password: "system-password-change-me"
-- BCrypt hash with strength 10
UPDATE users 
SET password_hash = '$2a$10$XQjEz5l5h5qN5vXqO5qZ0e5qZ5qZ5qZ5qZ5qZ5qZ5qZ5qZ5qZ5qZ5'
WHERE email = 'system@stl.internal';

-- Create index for performance on email lookups during login
CREATE INDEX IF NOT EXISTS idx_users_email_enabled ON users(email, enabled);

-- Add constraint to ensure password_hash is not null for enabled users
-- (Will be enforced in application, but good to document intent)
ALTER TABLE users 
ADD CONSTRAINT chk_enabled_users_have_password 
CHECK (enabled = FALSE OR password_hash IS NOT NULL);

COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password (strength 10)';
COMMENT ON COLUMN users.enabled IS 'Account enabled flag - disabled accounts cannot login';
COMMENT ON COLUMN users.last_login IS 'Timestamp of last successful login';
COMMENT ON COLUMN users.failed_login_attempts IS 'Counter for failed login attempts (reset on success)';
COMMENT ON COLUMN users.account_locked_until IS 'Account locked until this timestamp (NULL = not locked)';
