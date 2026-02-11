package org.example.waspapi.service;

import static org.example.waspapi.Constants.ALREADY_FRIENDS;
import static org.example.waspapi.Constants.CANNOT_FRIEND_SELF;
import static org.example.waspapi.Constants.FRIEND_NOT_FOUND;
import static org.example.waspapi.Constants.FRIEND_REQUEST_ALREADY_EXISTS;
import static org.example.waspapi.Constants.FRIEND_REQUEST_NOT_FOUND;
import static org.example.waspapi.Constants.USER_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.FriendRequest;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.FriendRequestRepository;
import org.example.waspapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FriendRequestService {

  private static final Logger logger = LoggerFactory.getLogger(FriendRequestService.class);

  private final FriendRequestRepository friendRequestRepository;
  private final UserRepository userRepository;

  public FriendRequestService(
      FriendRequestRepository friendRequestRepository, UserRepository userRepository) {
    this.friendRequestRepository = friendRequestRepository;
    this.userRepository = userRepository;
  }

  public FriendRequest create(UUID senderId, UUID receiverId) {
    logger.debug("Creating friend request from {} to {}", senderId, receiverId);

    if (senderId.equals(receiverId)) {
      throw new HandledException(CANNOT_FRIEND_SELF, HttpStatus.BAD_REQUEST);
    }

    User sender =
        userRepository
            .findById(senderId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

    User receiver =
        userRepository
            .findById(receiverId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

    // Check both directions for existing requests
    Optional<FriendRequest> existingAtoB =
        friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    Optional<FriendRequest> existingBtoA =
        friendRequestRepository.findBySenderIdAndReceiverId(receiverId, senderId);

    if (existingAtoB.isPresent() || existingBtoA.isPresent()) {
      FriendRequest existing = existingAtoB.orElseGet(existingBtoA::get);
      if ("ACCEPTED".equals(existing.getStatus())) {
        throw new HandledException(ALREADY_FRIENDS, HttpStatus.CONFLICT);
      }
      throw new HandledException(FRIEND_REQUEST_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }

    FriendRequest friendRequest = new FriendRequest(sender, receiver, "PENDING");

    logger.info("Friend request created from {} to {}", senderId, receiverId);
    return friendRequestRepository.save(friendRequest);
  }

  public List<FriendRequest> getPendingReceived(UUID userId) {
    logger.debug("Fetching pending friend requests for user {}", userId);
    return friendRequestRepository.findByReceiverIdAndStatus(userId, "PENDING");
  }

  public FriendRequest accept(UUID requestId, UUID userId) {
    logger.debug("Accepting friend request {}", requestId);

    FriendRequest friendRequest =
        friendRequestRepository
            .findById(requestId)
            .orElseThrow(
                () -> new HandledException(FRIEND_REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (!friendRequest.getReceiver().getId().equals(userId)) {
      throw new HandledException(FRIEND_REQUEST_NOT_FOUND, HttpStatus.FORBIDDEN);
    }

    friendRequest.setStatus("ACCEPTED");

    logger.info("Friend request {} accepted", requestId);
    return friendRequestRepository.save(friendRequest);
  }

  public FriendRequest reject(UUID requestId, UUID userId) {
    logger.debug("Rejecting friend request {}", requestId);

    FriendRequest friendRequest =
        friendRequestRepository
            .findById(requestId)
            .orElseThrow(
                () -> new HandledException(FRIEND_REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (!friendRequest.getReceiver().getId().equals(userId)) {
      throw new HandledException(FRIEND_REQUEST_NOT_FOUND, HttpStatus.FORBIDDEN);
    }

    friendRequest.setStatus("REJECTED");

    logger.info("Friend request {} rejected", requestId);
    return friendRequestRepository.save(friendRequest);
  }

  public List<User> getFriends(UUID userId) {
    logger.debug("Fetching friends for user {}", userId);

    List<FriendRequest> acceptedRequests =
        friendRequestRepository.findByUserIdAndStatus(userId, "ACCEPTED");

    List<User> friends = new ArrayList<>();
    for (FriendRequest fr : acceptedRequests) {
      if (fr.getSender().getId().equals(userId)) {
        friends.add(fr.getReceiver());
      } else {
        friends.add(fr.getSender());
      }
    }

    return friends;
  }

  public void removeFriend(UUID userId, UUID friendUserId) {
    logger.debug("Removing friendship between {} and {}", userId, friendUserId);

    Optional<FriendRequest> requestAtoB =
        friendRequestRepository.findBySenderIdAndReceiverId(userId, friendUserId);
    Optional<FriendRequest> requestBtoA =
        friendRequestRepository.findBySenderIdAndReceiverId(friendUserId, userId);

    FriendRequest friendRequest = null;
    if (requestAtoB.isPresent() && "ACCEPTED".equals(requestAtoB.get().getStatus())) {
      friendRequest = requestAtoB.get();
    } else if (requestBtoA.isPresent() && "ACCEPTED".equals(requestBtoA.get().getStatus())) {
      friendRequest = requestBtoA.get();
    }

    if (friendRequest == null) {
      throw new HandledException(FRIEND_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    friendRequestRepository.delete(friendRequest);

    logger.info("Friendship removed between {} and {}", userId, friendUserId);
  }
}
