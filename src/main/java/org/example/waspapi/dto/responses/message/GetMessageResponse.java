package org.example.waspapi.dto.responses.message;

import java.time.Instant;
import java.util.UUID;

public class GetMessageResponse {

  private UUID id;
  private String content;
  private String imageUrl;
  private Instant createdAt;
  private UUID userId;
  private String nickname;
  private String profilePhoto;

  public GetMessageResponse() {}

  public GetMessageResponse(
      UUID id,
      String content,
      String imageUrl,
      Instant createdAt,
      UUID userId,
      String nickname,
      String profilePhoto) {
    this.id = id;
    this.content = content;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
    this.userId = userId;
    this.nickname = nickname;
    this.profilePhoto = profilePhoto;
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

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getProfilePhoto() {
    return profilePhoto;
  }

  public void setProfilePhoto(String profilePhoto) {
    this.profilePhoto = profilePhoto;
  }
}
