package org.example.waspapi.dto.responses.joinrequest;

import java.time.Instant;
import java.util.UUID;

public class GetJoinRequestResponse {

  private UUID id;
  private UUID userId;
  private String userNickname;
  private String message;
  private String status;
  private Instant createdAt;

  public GetJoinRequestResponse() {}

  public GetJoinRequestResponse(
      UUID id, UUID userId, String userNickname, String message, String status, Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.userNickname = userNickname;
    this.message = message;
    this.status = status;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getUserNickname() {
    return userNickname;
  }

  public void setUserNickname(String userNickname) {
    this.userNickname = userNickname;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
