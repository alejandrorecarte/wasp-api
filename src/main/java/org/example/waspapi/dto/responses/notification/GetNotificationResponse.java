package org.example.waspapi.dto.responses.notification;

import java.time.Instant;
import java.util.UUID;

public class GetNotificationResponse {

  private UUID id;
  private String type;
  private UUID referenceId;
  private Boolean isRead;
  private Instant createdAt;

  public GetNotificationResponse() {}

  public GetNotificationResponse(
      UUID id, String type, UUID referenceId, Boolean isRead, Instant createdAt) {
    this.id = id;
    this.type = type;
    this.referenceId = referenceId;
    this.isRead = isRead;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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
