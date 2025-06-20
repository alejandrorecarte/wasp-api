package org.example.waspapi.dto.requests.users;

public class UpdateUserRequest {

  private String nickname;
  private String bio;
  private String preference;
  private String disponibility;
  private String profilePhoto;

  public UpdateUserRequest() {}

  public UpdateUserRequest(
      String nickname, String bio, String preference, String disponibility, String profilePhoto) {
    this.nickname = nickname;
    this.bio = bio;
    this.preference = preference;
    this.disponibility = disponibility;
    this.profilePhoto = profilePhoto;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getPreference() {
    return preference;
  }

  public void setPreference(String preference) {
    this.preference = preference;
  }

  public String getDisponibility() {
    return disponibility;
  }

  public void setDisponibility(String disponibility) {
    this.disponibility = disponibility;
  }

  public String getProfilePhoto() {
    return profilePhoto;
  }

  public void setProfilePhoto(String profilePhoto) {
    this.profilePhoto = profilePhoto;
  }
}
