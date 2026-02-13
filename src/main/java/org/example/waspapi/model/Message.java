package org.example.waspapi.model;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "messages", schema = "public")
public class Message {

  @Id
  @GeneratedValue
  @Column(name = "message_id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "game_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "messages_game_id_fkey"))
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "messages_user_id_fkey"))
  private User user;

  @Column(columnDefinition = "text")
  private String content;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public Message() {}

  public Message(Game game, User user, String content) {
    this.game = game;
    this.user = user;
    this.content = content;
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
