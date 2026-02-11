package org.example.waspapi.model;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "friend_requests", schema = "public")
public class FriendRequest {

  @Id
  @GeneratedValue
  @Column(name = "friend_request_id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "sender_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "friend_requests_sender_id_fkey"))
  private User sender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "receiver_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "friend_requests_receiver_id_fkey"))
  private User receiver;

  @Column(nullable = false)
  private String status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public FriendRequest() {}

  public FriendRequest(User sender, User receiver, String status) {
    this.sender = sender;
    this.receiver = receiver;
    this.status = status;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
