package org.example.waspapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions")
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "game_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "Subscriptions_game_id_fkey"))
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_email",
      referencedColumnName = "email",
      foreignKey = @ForeignKey(name = "Subscriptions_user_email_fkey"))
  private User user;

  @Column(name = "game_nickname")
  private String gameNickname;

  @Column(name = "role")
  private String role;

  @Column(name = "is_admin")
  private Boolean isAdmin;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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
