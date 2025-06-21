package org.example.waspapi.dto.requests.subscription;

public class CreateSubscriptionRequest {

  private String userEmail;
  private Long gameId;
  private String gameNickname;
  private String role;
  private Boolean isAdmin;

  public CreateSubscriptionRequest() {}

  public CreateSubscriptionRequest(
      String userEmail, Long gameId, String gameNickname, String role, Boolean isAdmin) {
    this.userEmail = userEmail;
    this.gameId = gameId;
    this.gameNickname = gameNickname;
    this.role = role;
    this.isAdmin = isAdmin;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public Long getGameId() {
    return gameId;
  }

  public void setGameId(Long gameId) {
    this.gameId = gameId;
  }

  public String getGameNickname() {
    return gameNickname;
  }

  public void setGameNickname(String gameNickname) {
    this.gameNickname = gameNickname;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Boolean getAdmin() {
    return isAdmin;
  }

  public void setAdmin(Boolean admin) {
    isAdmin = admin;
  }
}
