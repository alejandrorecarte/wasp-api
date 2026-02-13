package org.example.waspapi.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "character_sheets", schema = "public")
public class CharacterSheet {

  @Id
  @GeneratedValue
  @Column(name = "character_sheet_id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "character_sheets_user_id_fkey"))
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", foreignKey = @ForeignKey(name = "character_sheets_game_id_fkey"))
  private Game game;

  @Column(nullable = false)
  private String name;

  @Column private String role;

  @Column(columnDefinition = "text")
  private String description;

  @Column private Integer level;

  @Column(name = "character_photo")
  private String characterPhoto;

  @Column(name = "created_at", columnDefinition = "timestamptz")
  private OffsetDateTime createdAt;

  public CharacterSheet() {}

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

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

  public String getCharacterPhoto() {
    return characterPhoto;
  }

  public void setCharacterPhoto(String characterPhoto) {
    this.characterPhoto = characterPhoto;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
