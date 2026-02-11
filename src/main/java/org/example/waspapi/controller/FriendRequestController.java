package org.example.waspapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.waspapi.dto.requests.friendrequest.CreateFriendRequestRequest;
import org.example.waspapi.dto.responses.friendrequest.GetFriendRequestResponse;
import org.example.waspapi.model.FriendRequest;
import org.example.waspapi.model.User;
import org.example.waspapi.service.FriendRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Friends", description = "Endpoints for managing friendships and friend requests")
@RequestMapping("/friends")
public class FriendRequestController {

  private static final Logger logger = LoggerFactory.getLogger(FriendRequestController.class);
  private final FriendRequestService friendRequestService;

  public FriendRequestController(FriendRequestService friendRequestService) {
    this.friendRequestService = friendRequestService;
  }

  @Operation(
      summary = "Send a friend request",
      description = "Sends a friend request from the authenticated user to the specified user.",
      operationId = "createFriendRequest")
  @PostMapping("/requests")
  public ResponseEntity<GetFriendRequestResponse> createFriendRequest(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @RequestBody CreateFriendRequestRequest request) {
    UUID senderId = UUID.fromString(jwt.getSubject());
    logger.info("User {} sending friend request to {}", senderId, request.getReceiverUserId());

    FriendRequest friendRequest =
        friendRequestService.create(senderId, request.getReceiverUserId());

    logger.info("Friend request created with ID: {}", friendRequest.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(friendRequest));
  }

  @Operation(
      summary = "Get pending friend requests",
      description = "Returns all pending friend requests received by the authenticated user.",
      operationId = "getPendingFriendRequests")
  @GetMapping("/requests/pending")
  public ResponseEntity<List<GetFriendRequestResponse>> getPendingFriendRequests(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("Fetching pending friend requests for user {}", userId);

    List<GetFriendRequestResponse> responses =
        friendRequestService.getPendingReceived(userId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(responses);
  }

  @Operation(
      summary = "Accept a friend request",
      description = "Accepts a pending friend request. Only the receiver can accept.",
      operationId = "acceptFriendRequest")
  @PostMapping("/requests/{requestId}/accept")
  public ResponseEntity<GetFriendRequestResponse> acceptFriendRequest(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID requestId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} accepting friend request {}", userId, requestId);

    FriendRequest friendRequest = friendRequestService.accept(requestId, userId);
    return ResponseEntity.ok(toResponse(friendRequest));
  }

  @Operation(
      summary = "Reject a friend request",
      description = "Rejects a pending friend request. Only the receiver can reject.",
      operationId = "rejectFriendRequest")
  @PostMapping("/requests/{requestId}/reject")
  public ResponseEntity<GetFriendRequestResponse> rejectFriendRequest(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID requestId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} rejecting friend request {}", userId, requestId);

    FriendRequest friendRequest = friendRequestService.reject(requestId, userId);
    return ResponseEntity.ok(toResponse(friendRequest));
  }

  @Operation(
      summary = "Get friends list",
      description = "Returns all friends of the authenticated user.",
      operationId = "getFriends")
  @GetMapping
  public ResponseEntity<List<GetFriendResponse>> getFriends(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("Fetching friends for user {}", userId);

    List<GetFriendResponse> responses =
        friendRequestService.getFriends(userId).stream()
            .map(this::toFriendResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(responses);
  }

  @Operation(
      summary = "Remove a friend",
      description = "Removes an existing friendship with the specified user.",
      operationId = "removeFriend")
  @DeleteMapping("/{friendUserId}")
  public ResponseEntity<Void> removeFriend(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID friendUserId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} removing friend {}", userId, friendUserId);

    friendRequestService.removeFriend(userId, friendUserId);
    return ResponseEntity.noContent().build();
  }

  private GetFriendRequestResponse toResponse(FriendRequest friendRequest) {
    return new GetFriendRequestResponse(
        friendRequest.getId(),
        friendRequest.getSender().getId(),
        friendRequest.getSender().getNickname(),
        friendRequest.getReceiver().getId(),
        friendRequest.getReceiver().getNickname(),
        friendRequest.getStatus(),
        friendRequest.getCreatedAt());
  }

  private GetFriendResponse toFriendResponse(User user) {
    return new GetFriendResponse(user.getId(), user.getNickname(), user.getProfilePhoto());
  }

  static class GetFriendResponse {
    private UUID userId;
    private String nickname;
    private String profilePhoto;

    public GetFriendResponse() {}

    public GetFriendResponse(UUID userId, String nickname, String profilePhoto) {
      this.userId = userId;
      this.nickname = nickname;
      this.profilePhoto = profilePhoto;
    }

    public UUID getUserId() {
      return userId;
    }

    public void setUserId(UUID userId) {
      this.userId = userId;
    }

    public String getNickname() {
      return nickname;
    }

    public void setNickname(String nickname) {
      this.nickname = nickname;
    }

    public String getProfilePhoto() {
      return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
      this.profilePhoto = profilePhoto;
    }
  }
}
