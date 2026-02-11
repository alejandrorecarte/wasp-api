package org.example.waspapi.dto.requests.friendrequest;

import java.util.UUID;

public class CreateFriendRequestRequest {

  private UUID receiverUserId;

  public CreateFriendRequestRequest() {}

  public CreateFriendRequestRequest(UUID receiverUserId) {
    this.receiverUserId = receiverUserId;
  }

  public UUID getReceiverUserId() {
    return receiverUserId;
  }

  public void setReceiverUserId(UUID receiverUserId) {
    this.receiverUserId = receiverUserId;
  }
}
