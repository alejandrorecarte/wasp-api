package org.example.waspapi.dto.responses.character;

import java.time.OffsetDateTime;
import java.util.UUID;

public class GetCharacterSheetResponse {

  private UUID id;
  private String name;
  private String role;
  private String description;
  private Integer level;
  private String characterPhoto;
  private OffsetDateTime createdAt;
  private UUID userId;
  private String userNickname;

  public GetCharacterSheetResponse() {}

  public GetCharacterSheetResponse(
      UUID id,
      String name,
      String role,
      String description,
      Integer level,
      String characterPhoto,
      OffsetDateTime createdAt,
      UUID userId,
      String userNickname) {
    this.id = id;
    this.name = name;
    this.role = role;
    this.description = description;
    this.level = level;
    this.characterPhoto = characterPhoto;
    this.createdAt = createdAt;
    this.userId = userId;
    this.userNickname = userNickname;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public String getCharacterPhoto() {
    return characterPhoto;
  }

  public void setCharacterPhoto(String characterPhoto) {
    this.characterPhoto = characterPhoto;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
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
}
