package org.example.waspapi.dto.responses.game;

import java.util.UUID;

public class UpdateGameResponse {

  private String name;
  private String description;
  private String gamePhoto;
  private Short maxPlayers;
  private Boolean isPublic;
  private UUID themeId;

  public UpdateGameResponse() {}

  public UpdateGameResponse(
      String name,
      String description,
      String gamePhoto,
      Short maxPlayers,
      Boolean isPublic,
      UUID themeId) {
    this.name = name;
    this.description = description;
    this.gamePhoto = gamePhoto;
    this.maxPlayers = maxPlayers;
    this.isPublic = isPublic;
    this.themeId = themeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getGamePhoto() {
    return gamePhoto;
  }

  public void setGamePhoto(String gamePhoto) {
    this.gamePhoto = gamePhoto;
  }

  public Short getMaxPlayers() {
    return maxPlayers;
  }

  public void setMaxPlayers(Short maxPlayers) {
    this.maxPlayers = maxPlayers;
  }

  public Boolean getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
  }

  public UUID getThemeId() {
    return themeId;
  }

  public void setThemeId(UUID themeId) {
    this.themeId = themeId;
  }
}
