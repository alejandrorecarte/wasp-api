package org.example.waspapi.dto.requests.message;

public class SendMessageRequest {

  private String content;

  public SendMessageRequest() {}

  public SendMessageRequest(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
