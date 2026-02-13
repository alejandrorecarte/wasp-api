package org.example.waspapi.controller;

import static org.example.waspapi.Constants.INVALID_FILE_TYPE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.example.waspapi.dto.requests.privatemessage.SendPrivateMessageRequest;
import org.example.waspapi.dto.responses.privatemessage.GetConversationResponse;
import org.example.waspapi.dto.responses.privatemessage.GetPrivateMessageResponse;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.PrivateMessage;
import org.example.waspapi.service.PrivateMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "Private Messages", description = "Endpoints for private messaging between friends")
@RequestMapping("/friends")
public class PrivateMessageController {

  private static final Logger logger = LoggerFactory.getLogger(PrivateMessageController.class);
  private final PrivateMessageService privateMessageService;

  public PrivateMessageController(PrivateMessageService privateMessageService) {
    this.privateMessageService = privateMessageService;
  }

  @Operation(
      summary = "Send a private message",
      description = "Sends a private message to a friend. Requires an existing friendship.",
      operationId = "sendPrivateMessage")
  @PostMapping("/{friendUserId}/messages")
  public ResponseEntity<GetPrivateMessageResponse> sendMessage(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID friendUserId,
      @Valid @RequestBody SendPrivateMessageRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} sending private message to {}", userId, friendUserId);

    PrivateMessage message = privateMessageService.send(userId, friendUserId, request.getContent());
    return ResponseEntity.ok(toResponse(message));
  }

  @Operation(
      summary = "Send a private message with an image",
      description =
          "Sends a private message with an image to a friend. Text content is optional. Requires an existing friendship.",
      operationId = "sendPrivateMessageWithImage")
  @PostMapping(
      value = "/{friendUserId}/messages/image",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<GetPrivateMessageResponse> sendMessageWithImage(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID friendUserId,
      @RequestParam("file") MultipartFile file,
      @RequestParam(required = false) String content)
      throws IOException {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} sending image private message to {}", userId, friendUserId);

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new HandledException(INVALID_FILE_TYPE, HttpStatus.BAD_REQUEST);
    }

    PrivateMessage message =
        privateMessageService.sendWithImage(
            userId, friendUserId, content, file.getBytes(), contentType);
    return ResponseEntity.ok(toResponse(message));
  }

  @Operation(
      summary = "Get conversation with a friend",
      description =
          "Returns paginated messages between the authenticated user and a friend,"
              + " ordered by newest first.",
      operationId = "getPrivateMessages")
  @GetMapping("/{friendUserId}/messages")
  public ResponseEntity<Page<GetPrivateMessageResponse>> getMessages(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID friendUserId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info(
        "User {} fetching messages with {}, page={}, size={}", userId, friendUserId, page, size);

    Page<GetPrivateMessageResponse> messages =
        privateMessageService
            .getConversation(userId, friendUserId, PageRequest.of(page, size))
            .map(this::toResponse);
    return ResponseEntity.ok(messages);
  }

  @Operation(
      summary = "Get all conversations",
      description = "Returns a list of all conversations with the latest message preview for each.",
      operationId = "getConversations")
  @GetMapping("/conversations")
  public ResponseEntity<List<GetConversationResponse>> getConversations(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} fetching conversations", userId);

    List<GetConversationResponse> conversations = privateMessageService.getConversations(userId);
    return ResponseEntity.ok(conversations);
  }

  private GetPrivateMessageResponse toResponse(PrivateMessage message) {
    return new GetPrivateMessageResponse(
        message.getId(),
        message.getContent(),
        privateMessageService.resolveImageUrl(message.getImageUrl()),
        message.getCreatedAt(),
        message.getSender().getId(),
        message.getSender().getNickname(),
        message.getSender().getProfilePhoto());
  }
}
