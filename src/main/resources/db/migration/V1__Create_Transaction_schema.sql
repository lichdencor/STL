-- Secure Transaction Logger - Initial Schema

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- REFERENCE DATA TABLES
-- =====================================================

-- Currencies
CREATE TABLE currencies (
    code CHAR(3) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    symbol CHAR(3),
    precision INTEGER NOT NULL CHECK (precision >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE currencies IS 'ISO 4217 currency codes and metadata';

-- Transaction Types
CREATE TABLE transaction_types (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE transaction_types IS 'Defines transaction categories (PAYMENT, REFUND, etc.)';

-- =====================================================
-- USER AND ENTITY TABLES
-- =====================================================

-- Users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50), -- INGESTER, READER, AUDITOR, ADMIN
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

COMMENT ON TABLE users IS 'System users who can participate in transactions';

-- Business Entities
CREATE TABLE entities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- BANK, MERCHANT, SERVICE, GOVERNMENT, OTHER
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_entities_type ON entities(type);

COMMENT ON TABLE entities IS 'Business entities (banks, merchants, etc.) that participate in transactions';

-- =====================================================
-- CORE TRANSACTION TABLE (IMMUTABLE)
-- =====================================================

CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type_id UUID NOT NULL REFERENCES transaction_types(id),
    amount NUMERIC(20, 8) NOT NULL CHECK (amount >= 0),
    currency_code CHAR(3) NOT NULL REFERENCES currencies(code),
    payload JSONB,
    previous_hash CHAR(64), -- NULL for genesis transaction
    signature TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    immutable BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT chk_previous_hash_length CHECK (
        previous_hash IS NULL OR length(previous_hash) = 64
    )
);

CREATE INDEX idx_transactions_created_at ON transactions(created_at DESC);
CREATE INDEX idx_transactions_type_id ON transactions(type_id);
CREATE INDEX idx_transactions_currency_code ON transactions(currency_code);

COMMENT ON TABLE transactions IS 'Core immutable transaction records with hash chaining';
COMMENT ON COLUMN transactions.previous_hash IS 'Hash of previous transaction for tamper detection';
COMMENT ON COLUMN transactions.signature IS 'HMAC or digital signature for authenticity';

-- =====================================================
-- TRANSACTION RELATIONSHIPS (APPEND-ONLY)
-- =====================================================

-- Participants
CREATE TABLE participants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id UUID NOT NULL REFERENCES transactions(id),
    participant_type VARCHAR(20) NOT NULL, -- USER, ENTITY
    participant_id UUID NOT NULL, -- References users.id or entities.id
    role VARCHAR(20) NOT NULL, -- SENDER, RECEIVER, APPROVER, FEE, TAX
    amount NUMERIC(20, 8) CHECK (amount >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    CONSTRAINT uq_participant_role UNIQUE (transaction_id, participant_id, role)
);

CREATE INDEX idx_participants_transaction ON participants(transaction_id);
CREATE INDEX idx_participants_participant ON participants(participant_id, participant_type);

COMMENT ON TABLE participants IS 'Links users/entities to transactions with specific roles';

-- Transaction Status History (APPEND-ONLY)
CREATE TABLE transaction_status_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id UUID NOT NULL REFERENCES transactions(id),
    status VARCHAR(20) NOT NULL, -- PENDING, ACTIVE, ON_HOLD, APPROVED, CANCELED, FAILED, REFUND, PARTIAL
    reason TEXT,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_status_history_transaction ON transaction_status_history(transaction_id, updated_at DESC);

COMMENT ON TABLE transaction_status_history IS 'Append-only status change log for transactions';

-- Transaction Audit (APPEND-ONLY)
CREATE TABLE transaction_audit (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id UUID NOT NULL REFERENCES transactions(id),
    actor_type VARCHAR(20) NOT NULL, -- USER, ENTITY, SYSTEM
    actor_id UUID, -- Nullable for SYSTEM actions
    action_type VARCHAR(30) NOT NULL, -- CREATE, UPDATE_STATUS, REFUND, CANCEL, APPROVE, REJECT, LOCK, UNLOCK
    metadata JSONB,
    previous_hash CHAR(64),
    signature TEXT,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_transaction ON transaction_audit(transaction_id, timestamp DESC);
CREATE INDEX idx_audit_actor ON transaction_audit(actor_id, timestamp DESC);

COMMENT ON TABLE transaction_audit IS 'Tamper-evident audit trail for all transaction actions';

-- Transaction Locks (APPEND-ONLY)
CREATE TABLE transaction_locks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id UUID NOT NULL REFERENCES transactions(id),
    lock_type VARCHAR(30) NOT NULL, -- HOLD, MANUAL_APPROVAL
    locked_by UUID, -- References users.id or entities.id
    reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ,
    
    CONSTRAINT chk_expires_after_created CHECK (
        expires_at IS NULL OR expires_at > created_at
    )
);

CREATE INDEX idx_locks_transaction ON transaction_locks(transaction_id);
CREATE INDEX idx_locks_expires ON transaction_locks(expires_at) WHERE expires_at IS NOT NULL;

COMMENT ON TABLE transaction_locks IS 'Holds and approval requirements for transactions';

-- =====================================================
-- SEED DATA FOR DEVELOPMENT
-- =====================================================

-- Insert common currencies
INSERT INTO currencies (code, name, symbol, precision) VALUES
    ('USD', 'US Dollar', '$', 2),
    ('EUR', 'Euro', '€', 2),
    ('GBP', 'British Pound', '£', 2),
    ('JPY', 'Japanese Yen', '¥', 0),
    ('BTC', 'Bitcoin', '₿', 8),
    ('ETH', 'Ethereum', 'Ξ', 18);

-- Insert common transaction types
INSERT INTO transaction_types (name, description) VALUES
    ('PAYMENT', 'Standard payment transaction'),
    ('REFUND', 'Refund of a previous payment'),
    ('TRANSFER', 'Transfer between accounts'),
    ('WITHDRAWAL', 'Cash withdrawal'),
    ('DEPOSIT', 'Cash or check deposit'),
    ('FEE', 'Service fee charge'),
    ('ADJUSTMENT', 'Manual adjustment or correction');

-- Insert a system user for automated actions
INSERT INTO users (name, email, role) VALUES
    ('System', 'system@stl.internal', 'ADMIN');
