package org.example.waspapi.dto.requests.joinrequest;

public class CreateJoinRequestRequest {

  private String message;

  public CreateJoinRequestRequest() {}

  public CreateJoinRequestRequest(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
