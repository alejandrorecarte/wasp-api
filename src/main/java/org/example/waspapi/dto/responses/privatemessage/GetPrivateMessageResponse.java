package org.example.waspapi.dto.responses.privatemessage;

import java.time.Instant;
import java.util.UUID;

public class GetPrivateMessageResponse {

  private UUID id;
  private String content;
  private String imageUrl;
  private Instant createdAt;
  private UUID senderUserId;
  private String senderNickname;
  private String senderProfilePhoto;

  public GetPrivateMessageResponse() {}

  public GetPrivateMessageResponse(
      UUID id,
      String content,
      String imageUrl,
      Instant createdAt,
      UUID senderUserId,
      String senderNickname,
      String senderProfilePhoto) {
    this.id = id;
    this.content = content;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
    this.senderUserId = senderUserId;
    this.senderNickname = senderNickname;
    this.senderProfilePhoto = senderProfilePhoto;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
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

  public String getSenderProfilePhoto() {
    return senderProfilePhoto;
  }

  public void setSenderProfilePhoto(String senderProfilePhoto) {
    this.senderProfilePhoto = senderProfilePhoto;
  }
}
