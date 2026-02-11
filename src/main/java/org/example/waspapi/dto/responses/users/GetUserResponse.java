package org.example.waspapi.dto.responses.users;

import java.util.UUID;

public class GetUserResponse {

  private UUID id;
  private String profilePhoto;

  public GetUserResponse() {}

  public GetUserResponse(UUID id, String profilePhoto) {
    this.id = id;
    this.profilePhoto = profilePhoto;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getProfilePhoto() {
    return profilePhoto;
  }

  public void setProfilePhoto(String profilePhoto) {
    this.profilePhoto = profilePhoto;
  }
}
