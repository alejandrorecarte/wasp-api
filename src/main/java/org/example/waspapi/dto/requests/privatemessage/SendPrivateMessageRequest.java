package org.example.waspapi.dto.requests.privatemessage;

public class SendPrivateMessageRequest {

  private String content;

  public SendPrivateMessageRequest() {}

  public SendPrivateMessageRequest(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
