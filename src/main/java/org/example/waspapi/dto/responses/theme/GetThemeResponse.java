package org.example.waspapi.dto.responses.theme;

import java.util.UUID;

public class GetThemeResponse {

  private UUID themeId;
  private String name;
  private String description;
  private String themePhoto;

  public GetThemeResponse() {}

  public GetThemeResponse(UUID themeId, String name, String description, String themePhoto) {
    this.themeId = themeId;
    this.name = name;
    this.description = description;
    this.themePhoto = themePhoto;
  }

  public UUID getThemeId() {
    return themeId;
  }

  public void setThemeId(UUID themeId) {
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

  public String getThemePhoto() {
    return themePhoto;
  }

  public void setThemePhoto(String themePhoto) {
    this.themePhoto = themePhoto;
  }
}
