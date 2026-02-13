package org.example.waspapi.exceptions;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(HandledException.class)
  public ResponseEntity<String> handleHandledException(HandledException e) {
    logger.error("Handled exception: {}", e.getMessage());
    return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    logger.warn("Invalid path parameter: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameter: " + e.getName());
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<String> handleMissingPart(MissingServletRequestPartException e) {
    logger.warn("Missing request part: {}", e.getRequestPartName());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(ClientAbortException.class)
  public void handleClientAbort(ClientAbortException e) {
    logger.debug("Client disconnected: {}", e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Void> handleGenericException(Exception e) {
    logger.error("Unexpected error: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
