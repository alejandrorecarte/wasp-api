package org.example.waspapi.dto.responses.friendrequest;

import java.time.Instant;
import java.util.UUID;

public class GetFriendRequestResponse {

  private UUID id;
  private UUID senderUserId;
  private String senderNickname;
  private UUID receiverUserId;
  private String receiverNickname;
  private String status;
  private Instant createdAt;

  public GetFriendRequestResponse() {}

  public GetFriendRequestResponse(
      UUID id,
      UUID senderUserId,
      String senderNickname,
      UUID receiverUserId,
      String receiverNickname,
      String status,
      Instant createdAt) {
    this.id = id;
    this.senderUserId = senderUserId;
    this.senderNickname = senderNickname;
    this.receiverUserId = receiverUserId;
    this.receiverNickname = receiverNickname;
    this.status = status;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getSenderUserId() {
    return senderUserId;
  }

  public void setSenderUserId(UUID senderUserId) {
    this.senderUserId = senderUserId;
  }

  public String getSenderNickname() {
    return senderNickname;
  }

  public void setSenderNickname(String senderNickname) {
    this.senderNickname = senderNickname;
  }

  public UUID getReceiverUserId() {
    return receiverUserId;
  }

  public void setReceiverUserId(UUID receiverUserId) {
    this.receiverUserId = receiverUserId;
  }

  public String getReceiverNickname() {
    return receiverNickname;
  }

  public void setReceiverNickname(String receiverNickname) {
    this.receiverNickname = receiverNickname;
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
