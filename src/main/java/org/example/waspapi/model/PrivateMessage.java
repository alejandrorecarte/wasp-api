package org.example.waspapi.model;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "private_messages", schema = "public")
public class PrivateMessage {

  @Id
  @GeneratedValue
  @Column(name = "private_message_id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "sender_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "private_messages_sender_id_fkey"))
  private User sender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "receiver_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "private_messages_receiver_id_fkey"))
  private User receiver;

  @Column(columnDefinition = "text")
  private String content;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public PrivateMessage() {}

  public PrivateMessage(User sender, User receiver, String content) {
    this.sender = sender;
    this.receiver = receiver;
    this.content = content;
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public User getReceiver() {
    return receiver;
  }

  public void setReceiver(User receiver) {
    this.receiver = receiver;
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
