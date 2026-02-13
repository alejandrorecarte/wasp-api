package org.example.waspapi.model;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "notifications", schema = "public")
public class Notification {

  @Id
  @GeneratedValue
  @Column(name = "notification_id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "notifications_user_id_fkey"))
  private User user;

  @Column(nullable = false)
  private String type;

  @Column(name = "reference_id", nullable = false)
  private UUID referenceId;

  @Column(name = "is_read", nullable = false)
  private Boolean isRead = false;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public Notification() {}

  public Notification(User user, String type, UUID referenceId) {
    this.user = user;
    this.type = type;
    this.referenceId = referenceId;
    this.isRead = false;
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public UUID getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(UUID referenceId) {
    this.referenceId = referenceId;
  }

  public Boolean getIsRead() {
    return isRead;
  }

  public void setIsRead(Boolean isRead) {
    this.isRead = isRead;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
