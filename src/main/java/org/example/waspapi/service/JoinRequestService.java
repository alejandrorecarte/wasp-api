package org.example.waspapi.service;

import static org.example.waspapi.Constants.ALREADY_SUBSCRIBED;
import static org.example.waspapi.Constants.GAME_FULL;
import static org.example.waspapi.Constants.GAME_NOT_FOUND;
import static org.example.waspapi.Constants.JOIN_REQUEST_ALREADY_EXISTS;
import static org.example.waspapi.Constants.JOIN_REQUEST_NOT_FOUND;
import static org.example.waspapi.Constants.USER_NOT_FOUND;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.example.waspapi.dto.requests.subscription.CreateSubscriptionRequest;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Game;
import org.example.waspapi.model.JoinRequest;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.GameRepository;
import org.example.waspapi.repository.JoinRequestRepository;
import org.example.waspapi.repository.SubscriptionRepository;
import org.example.waspapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class JoinRequestService {

  private static final Logger logger = LoggerFactory.getLogger(JoinRequestService.class);

  private final JoinRequestRepository joinRequestRepository;
  private final UserRepository userRepository;
  private final GameRepository gameRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionService subscriptionService;

  public JoinRequestService(
      JoinRequestRepository joinRequestRepository,
      UserRepository userRepository,
      GameRepository gameRepository,
      SubscriptionRepository subscriptionRepository,
      SubscriptionService subscriptionService) {
    this.joinRequestRepository = joinRequestRepository;
    this.userRepository = userRepository;
    this.gameRepository = gameRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.subscriptionService = subscriptionService;
  }

  public JoinRequest create(UUID userId, UUID gameId, String message) {
    logger.debug("Creating join request for user {} on game {}", userId, gameId);

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (subscriptionRepository.existsByUserIdAndGameId(userId, gameId)) {
      throw new HandledException(ALREADY_SUBSCRIBED, HttpStatus.CONFLICT);
    }

    if (joinRequestRepository.existsByUserIdAndGameId(userId, gameId)) {
      throw new HandledException(JOIN_REQUEST_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }

    if (game.getMaxPlayers() != null
        && subscriptionRepository.countByGameId(gameId) >= game.getMaxPlayers()) {
      throw new HandledException(GAME_FULL, HttpStatus.CONFLICT);
    }

    JoinRequest joinRequest = new JoinRequest(user, game, message, "PENDING");
    joinRequest.setCreatedAt(Instant.now());

    logger.info("Join request created for user {} on game {}", userId, gameId);
    return joinRequestRepository.save(joinRequest);
  }

  public boolean existsByUserAndGame(UUID userId, UUID gameId) {
    return joinRequestRepository.existsByUserIdAndGameId(userId, gameId);
  }

  public List<JoinRequest> getPendingByGameId(UUID gameId) {
    logger.debug("Fetching pending join requests for game {}", gameId);
    return joinRequestRepository.findByGameIdAndStatus(gameId, "PENDING");
  }

  public JoinRequest accept(UUID requestId, UUID adminUserId) {
    logger.debug("Accepting join request {}", requestId);

    JoinRequest joinRequest =
        joinRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new HandledException(JOIN_REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

    joinRequest.setStatus("ACCEPTED");
    joinRequestRepository.save(joinRequest);

    User user = joinRequest.getUser();
    Game game = joinRequest.getGame();

    CreateSubscriptionRequest subscriptionRequest =
        new CreateSubscriptionRequest(
            user.getId(), game.getId(), user.getNickname(), "PLAYER", false);
    subscriptionService.createSubscription(subscriptionRequest);

    logger.info(
        "Join request {} accepted, subscription created for user {}", requestId, user.getId());
    return joinRequest;
  }

  public JoinRequest reject(UUID requestId) {
    logger.debug("Rejecting join request {}", requestId);

    JoinRequest joinRequest =
        joinRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new HandledException(JOIN_REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

    joinRequest.setStatus("REJECTED");

    logger.info("Join request {} rejected", requestId);
    return joinRequestRepository.save(joinRequest);
  }
}
