package org.example.waspapi.model;

import javax.persistence.*;

@Entity
@Table(name = "users_games")
@IdClass(SubscriptionId.class)
public class Subscription {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "email", referencedColumnName = "email")
  private User user;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", referencedColumnName = "game_id")
  private Game game;

  @Column(name = "game_nickname")
  private String gameNickname;

  @Column(name = "role")
  private String role;

  @Column(name = "is_admin")
  private Boolean isAdmin;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
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
