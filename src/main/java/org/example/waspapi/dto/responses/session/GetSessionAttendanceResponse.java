package org.example.waspapi.dto.responses.session;

import java.util.UUID;

public class GetSessionAttendanceResponse {

  private UUID userId;
  private String nickname;
  private String profilePhoto;
  private Boolean confirmAssist;

  public GetSessionAttendanceResponse() {}

  public GetSessionAttendanceResponse(
      UUID userId, String nickname, String profilePhoto, Boolean confirmAssist) {
    this.userId = userId;
    this.nickname = nickname;
    this.profilePhoto = profilePhoto;
    this.confirmAssist = confirmAssist;
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

  public Boolean getConfirmAssist() {
    return confirmAssist;
  }

  public void setConfirmAssist(Boolean confirmAssist) {
    this.confirmAssist = confirmAssist;
  }
}
