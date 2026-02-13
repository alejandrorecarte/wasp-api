package org.example.waspapi.dto.requests.game;

import java.util.UUID;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class UpdateGameRequest {

  private String name;
  private String description;
  private String lore;

  @Min(1)
  @Max(99)
  private Short maxPlayers;

  private Boolean isPublic;
  private UUID themeId;

  public UpdateGameRequest() {}

  public UpdateGameRequest(
      String name, String description, Short maxPlayers, Boolean isPublic, UUID themeId) {
    this.name = name;
    this.description = description;
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

  public String getLore() {
    return lore;
  }

  public void setLore(String lore) {
    this.lore = lore;
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
