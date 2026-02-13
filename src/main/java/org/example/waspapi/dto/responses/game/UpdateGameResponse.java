package org.example.waspapi.dto.responses.game;

import java.util.UUID;

public class UpdateGameResponse {

  private String name;
  private String description;
  private String lore;
  private String gamePhoto;
  private Short maxPlayers;
  private Boolean isPublic;
  private String themeName;
  private UUID masterUserId;
  private long playerCount;

  public UpdateGameResponse() {}

  public UpdateGameResponse(
      String name,
      String description,
      String lore,
      String gamePhoto,
      Short maxPlayers,
      Boolean isPublic,
      String themeName,
      UUID masterUserId,
      long playerCount) {
    this.name = name;
    this.description = description;
    this.lore = lore;
    this.gamePhoto = gamePhoto;
    this.maxPlayers = maxPlayers;
    this.isPublic = isPublic;
    this.themeName = themeName;
    this.masterUserId = masterUserId;
    this.playerCount = playerCount;
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

  public String getLore() {
    return lore;
  }

  public void setLore(String lore) {
    this.lore = lore;
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

  public String getThemeName() {
    return themeName;
  }

  public void setThemeName(String themeName) {
    this.themeName = themeName;
  }

  public UUID getMasterUserId() {
    return masterUserId;
  }

  public void setMasterUserId(UUID masterUserId) {
    this.masterUserId = masterUserId;
  }

  public long getPlayerCount() {
    return playerCount;
  }

  public void setPlayerCount(long playerCount) {
    this.playerCount = playerCount;
  }
}
