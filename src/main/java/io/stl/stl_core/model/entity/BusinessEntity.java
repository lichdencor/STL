package io.stl.stl_core.model.entity;

import io.stl.stl_core.model.enums.EntityType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a business entity (Bank, Merchant, Service Provider, etc.)
 * that can participate in transactions.
 * 
 * Note: Named "BusinessEntity" to avoid conflict with JPA's Entity annotation.
 */
@Entity
@Table(name = "entities")
public class BusinessEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotBlank
  @Column(name = "name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private EntityType type;

  @Type(JsonBinaryType.class)
  @Column(name = "metadata", columnDefinition = "jsonb")
  private Map<String, Object> metadata;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }

  // Constructors
  public BusinessEntity() {
  }

  public BusinessEntity(String name, EntityType type) {
    this.name = name;
    this.type = type;
  }

  // Getters and Setters
  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public EntityType getType() {
    return type;
  }

  public void setType(EntityType type) {
    this.type = type;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof BusinessEntity))
      return false;
    BusinessEntity that = (BusinessEntity) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
