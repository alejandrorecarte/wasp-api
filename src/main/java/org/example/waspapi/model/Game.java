package org.example.waspapi.model;

import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "games", schema = "public")
public class Game {

  @Id
  @GeneratedValue
  @Column(name = "game_id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "text")
  private String description;

  @Column(name = "game_photo")
  private String gamePhoto;

  @Column(name = "max_players")
  private Short maxPlayers;

  @Column(name = "public")
  private Boolean isPublic;

  @ManyToOne
  @JoinColumn(name = "theme_id", foreignKey = @ForeignKey(name = "games_theme_id_fkey"))
  private Theme theme;

  @ManyToOne
  @JoinColumn(name = "master_user_id", foreignKey = @ForeignKey(name = "games_master_user_id_fkey"))
  private User masterUser;

  @Column(name = "is_deleted")
  private Boolean isDeleted = false;

  public Game() {}

  public Game(
      String name,
      String description,
      String gamePhoto,
      Short maxPlayers,
      Boolean isPublic,
      Theme theme) {
    this.name = name;
    this.description = description;
    this.gamePhoto = gamePhoto;
    this.maxPlayers = maxPlayers;
    this.isPublic = isPublic;
    this.theme = theme;
    this.isDeleted = false;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public Boolean getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
  }

  public Theme getTheme() {
    return theme;
  }

  public void setTheme(Theme theme) {
    this.theme = theme;
  }

  public User getMasterUser() {
    return masterUser;
  }

  public void setMasterUser(User masterUser) {
    this.masterUser = masterUser;
  }

  public Boolean getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }
}
