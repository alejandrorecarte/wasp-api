package org.example.waspapi.dto.requests.character;

import javax.validation.constraints.NotBlank;

public class CreateCharacterSheetRequest {

  @NotBlank private String name;

  private String role;

  private String description;

  private Integer level;

  public CreateCharacterSheetRequest() {}

  public CreateCharacterSheetRequest(String name, String role, String description, Integer level) {
    this.name = name;
    this.role = role;
    this.description = description;
    this.level = level;
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
}
