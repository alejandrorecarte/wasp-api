package org.example.waspapi.dto.requests.game;

import jakarta.validation.constraints.NotBlank;

public class CreateGameRequest {

  @NotBlank private String name;

  private String description;

  private String gamePhoto;

  private Short maxPlayers;

  private Boolean isPublic;

  public CreateGameRequest() {}

  public CreateGameRequest(
      String name, String description, String gamePhoto, Short maxPlayers, Boolean isPublic) {
    this.name = name;
    this.description = description;
    this.gamePhoto = gamePhoto;
    this.maxPlayers = maxPlayers;
    this.isPublic = isPublic;
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

  public Boolean getPublic() {
    return isPublic;
  }

  public void setPublic(Boolean aPublic) {
    isPublic = aPublic;
  }

  public Long getThemeId() {
    return themeId;
  }

  public void setThemeId(Long themeId) {
    this.themeId = themeId;
  }

  private Long themeId;
}
