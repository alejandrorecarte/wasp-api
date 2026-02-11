package org.example.waspapi.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(HandledException.class)
  public ResponseEntity<String> handleHandledException(HandledException e) {
    logger.error("Handled exception: {}", e.getMessage());
    return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Void> handleGenericException(Exception e) {
    logger.error("Unexpected error: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
