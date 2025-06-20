package org.example.waspapi.exceptions;

import org.springframework.http.HttpStatus;

public class HandledException extends RuntimeException {

  private HttpStatus statusCode;

  public HandledException(String message) {
    super(message);
  }

  public HandledException(String message, HttpStatus messageCode) {
    super(message);
    this.statusCode = messageCode;
  }

  public HttpStatus getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(HttpStatus statusCode) {
    this.statusCode = statusCode;
  }
}
