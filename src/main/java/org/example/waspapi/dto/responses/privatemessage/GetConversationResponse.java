package org.example.waspapi.dto.responses.privatemessage;

import java.time.Instant;
import java.util.UUID;

public class GetConversationResponse {

  private UUID friendUserId;
  private String friendNickname;
  private String friendProfilePhoto;
  private String lastMessageContent;
  private Instant lastMessageCreatedAt;
  private UUID lastMessageSenderUserId;

  public GetConversationResponse() {}

  public GetConversationResponse(
      UUID friendUserId,
      String friendNickname,
      String friendProfilePhoto,
      String lastMessageContent,
      Instant lastMessageCreatedAt,
      UUID lastMessageSenderUserId) {
    this.friendUserId = friendUserId;
    this.friendNickname = friendNickname;
    this.friendProfilePhoto = friendProfilePhoto;
    this.lastMessageContent = lastMessageContent;
    this.lastMessageCreatedAt = lastMessageCreatedAt;
    this.lastMessageSenderUserId = lastMessageSenderUserId;
  }

  public UUID getFriendUserId() {
    return friendUserId;
  }

  public void setFriendUserId(UUID friendUserId) {
    this.friendUserId = friendUserId;
  }

  public String getFriendNickname() {
    return friendNickname;
  }

  public void setFriendNickname(String friendNickname) {
    this.friendNickname = friendNickname;
  }

  public String getFriendProfilePhoto() {
    return friendProfilePhoto;
  }

  public void setFriendProfilePhoto(String friendProfilePhoto) {
    this.friendProfilePhoto = friendProfilePhoto;
  }

  public String getLastMessageContent() {
    return lastMessageContent;
  }

  public void setLastMessageContent(String lastMessageContent) {
    this.lastMessageContent = lastMessageContent;
  }

  public Instant getLastMessageCreatedAt() {
    return lastMessageCreatedAt;
  }

  public void setLastMessageCreatedAt(Instant lastMessageCreatedAt) {
    this.lastMessageCreatedAt = lastMessageCreatedAt;
  }

  public UUID getLastMessageSenderUserId() {
    return lastMessageSenderUserId;
  }

  public void setLastMessageSenderUserId(UUID lastMessageSenderUserId) {
    this.lastMessageSenderUserId = lastMessageSenderUserId;
  }
}
