package org.example.waspapi.service;

import static org.example.waspapi.Constants.GAME_NOT_FOUND;
import static org.example.waspapi.Constants.USER_NOT_FOUND;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.waspapi.dto.requests.subscription.CreateSubscriptionRequest;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Game;
import org.example.waspapi.model.Subscription;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.GameRepository;
import org.example.waspapi.repository.SubscriptionRepository;
import org.example.waspapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;
  private final GameRepository gameRepository;

  public SubscriptionService(
      SubscriptionRepository subscriptionRepository,
      UserRepository userRepository,
      GameRepository gameRepository) {
    this.subscriptionRepository = subscriptionRepository;
    this.userRepository = userRepository;
    this.gameRepository = gameRepository;
  }

  /**
   * Creates a new subscription in the system.
   *
   * <p>This method validates that the user and the game exist in the system before creating a
   * subscription. If the user or the game are not found, it throws a HandledException. Then, it
   * builds a Subscription object with the provided data and saves it in the repository.
   *
   * @param request A CreateSubscriptionRequest object containing the necessary data to create the
   *     subscription.
   * @return The created and saved Subscription object.
   * @throws HandledException If the user or the game are not found.
   */
  public Subscription createSubscription(CreateSubscriptionRequest request) {
    User user =
        userRepository
            .findById(request.getUserId())
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    Game game =
        gameRepository
            .findById(request.getGameId())
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));

    Subscription subscription = new Subscription();
    subscription.setUser(user);
    subscription.setGame(game);
    subscription.setGameNickname(request.getGameNickname());
    subscription.setRole(request.getRole());
    subscription.setAdmin(request.getAdmin());

    return subscriptionRepository.save(subscription);
  }

  /**
   * Checks if a user is subscribed to a specific game.
   *
   * <p>This method searches the subscription repository and verifies if there is any subscription
   * that matches the provided user's email and game ID.
   *
   * @param userEmail The email of the user.
   * @param gameId The unique identifier of the game.
   * @return true if the user is subscribed to the game, false otherwise.
   */
  public boolean isSubscribed(UUID userId, UUID gameId) {
    return subscriptionRepository.existsByUserIdAndGameId(userId, gameId);
  }

  public boolean isAdmin(UUID userId, UUID gameId) {
    return subscriptionRepository.existsByUserIdAndGameIdAndIsAdminTrue(userId, gameId);
  }

  public long countPlayersByGameId(UUID gameId) {
    return subscriptionRepository.countByGameId(gameId);
  }

  public List<Subscription> getSubscriptionsByGameId(UUID gameId) {
    return subscriptionRepository.findByGameId(gameId);
  }

  public List<Game> getGamesByUserId(UUID userId) {
    return subscriptionRepository.findByUserId(userId).stream()
        .map(Subscription::getGame)
        .filter(game -> !Boolean.TRUE.equals(game.getIsDeleted()))
        .collect(Collectors.toList());
  }
}
