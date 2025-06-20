package org.example.waspapi.controller;

import static org.example.waspapi.Constants.AUTH0_AUDIENCE_EMAIL;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.waspapi.dto.requests.game.CreateGameRequest;
import org.example.waspapi.dto.requests.game.UpdateGameRequest;
import org.example.waspapi.dto.requests.subscription.CreateSubscriptionRequest;
import org.example.waspapi.dto.responses.game.GetGameResponse;
import org.example.waspapi.dto.responses.game.UpdateGameResponse;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Game;
import org.example.waspapi.service.GameService;
import org.example.waspapi.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Games", description = "Endpoints for managing games")
@RequestMapping("/games")
public class GameController {

  private static final Logger logger = LoggerFactory.getLogger(GameController.class);
  private final GameService gameService;
  private final SubscriptionService subscriptionService;

  public GameController(GameService gameService, SubscriptionService subscriptionService) {
    this.gameService = gameService;
    this.subscriptionService = subscriptionService;
  }

  /**
   * Endpoint to create a new game.
   *
   * <p>This method handles HTTP POST requests to create a new game. It uses the authenticated
   * user's JWT token to extract their email and associates the created game with the user as the
   * owner. Additionally, a subscription is created for the user who created the game. In case of
   * any errors during the process, a 500 Internal Server Error response is sent.
   *
   * @param jwt The JWT token of the authenticated user, used to extract claims.
   * @param request The request body containing the details of the game to be created, validated for
   *     correctness.
   * @return A ResponseEntity containing the created game object: - 200 OK if the game is
   *     successfully created. - 400 Bad Request if the request is invalid or handled exception
   *     occurs. - 500 Internal Server Error if an exception occurs.
   */
  @Operation(
      summary = "Create a new game",
      description =
          "Creates a new game and associates it with the authenticated user as the owner. "
              + "A subscription is also created for the user.",
      operationId = "createGame")
  @PostMapping("/create")
  public ResponseEntity<Long> createGame(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateGameRequest request) {
    try {
      logger.info("Creating game with request: {}", request);
      String auth0Email = jwt.getClaim(AUTH0_AUDIENCE_EMAIL).toString();
      Game game = gameService.createGame(request);

      // Create a subscription for the user who created the game
      CreateSubscriptionRequest subscription =
          new CreateSubscriptionRequest(auth0Email, game.getId(), request.getName(), "OWNER", true);
      subscriptionService.createSubscription(subscription);

      logger.info("Game created successfully with ID: {}", game.getId());
      return ResponseEntity.ok(game.getId());
    } catch (HandledException e) {
      logger.error("Handled exception while creating game: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(null);
    } catch (Exception e) {
      logger.error("Error creating game: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Endpoint to fetch a game by its ID.
   *
   * <p>This method handles HTTP GET requests to retrieve a game's details. It verifies if the
   * authenticated user is subscribed to the specified game before proceeding with the fetch. If the
   * user is not subscribed, a 403 Forbidden response is returned. If the game is marked as deleted,
   * a 404 Not Found response is returned. In case of any errors during the process, a 500 Internal
   * Server Error response is sent.
   *
   * @param jwt The JWT token of the authenticated user, used to extract claims.
   * @param gameId The ID of the game to be fetched, provided as a path variable.
   * @return A ResponseEntity containing the game object: - 200 OK if the game is successfully
   *     fetched. - 400 Bad Request if the request is invalid or handled exception occurs. - 403
   *     Forbidden if the user is not subscribed. - 404 Not Found if the game is marked as deleted.
   *     - 500 Internal Server Error if an exception occurs.
   */
  @Operation(
      summary = "Get a game by ID",
      description =
          "Fetches a game by its ID. The user must be subscribed to the game to access its details.",
      operationId = "getGame")
  @GetMapping("/{gameId}")
  public ResponseEntity<GetGameResponse> getGame(
      @AuthenticationPrincipal Jwt jwt, @PathVariable Long gameId) {
    try {
      logger.info("Fetching game with ID: {}", gameId);
      String auth0Email = jwt.getClaim(AUTH0_AUDIENCE_EMAIL).toString();
      if (!subscriptionService.isSubscribed(auth0Email, gameId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }

      Game game = gameService.getGameById(gameId);
      if (game.getIsDeleted()) {
        return ResponseEntity.notFound().build();
      }
      logger.info("Game with ID {} fetched successfully", gameId);

      GetGameResponse response =
          new GetGameResponse(
              game.getName(),
              game.getDescription(),
              game.getGamePhoto(),
              game.getMaxPlayers(),
              game.getIsPublic(),
              game.getTheme() == null ? null : game.getTheme().getId());
      return ResponseEntity.ok(response);
    } catch (HandledException e) {
      logger.error("Handled exception while fetching game: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(null);
    } catch (Exception e) {
      logger.error("Error fetching game: {}", e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Endpoint to update a game by its ID.
   *
   * <p>This method handles HTTP PUT requests to update a game's details. It verifies if the
   * authenticated user has admin privileges for the specified game before proceeding with the
   * update. If the user is not authorized, a 403 Forbidden response is returned. In case of any
   * errors during the process, a 500 Internal Server Error response is sent.
   *
   * @param jwt The JWT token of the authenticated user, used to extract claims.
   * @param gameId The ID of the game to be updated, provided as a path variable.
   * @param request The request body containing the updated game details, validated for correctness.
   * @return A ResponseEntity containing the updated game object: - 200 OK if the game is
   *     successfully updated. - 400 Bad Request if the request is invalid or handled exception
   *     occurs. - 403 Forbidden if the user is not authorized. - 404 Not Found if the game does not
   *     exist. - 500 Internal Server Error if an exception occurs.
   */
  @Operation(
      summary = "Update a game by ID",
      description =
          "Updates a game by its ID. The user must have admin privileges for the game to perform the update.",
      operationId = "updateGame")
  @PutMapping("/{gameId}/update")
  public ResponseEntity<UpdateGameResponse> updateGame(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable Long gameId,
      @Valid @RequestBody UpdateGameRequest request) {
    try {
      logger.info("Updating game with ID: {} and request: {}", gameId, request);
      if (!subscriptionService.isAdmin(jwt.getClaim(AUTH0_AUDIENCE_EMAIL).toString(), gameId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
      Game game = gameService.updateGame(gameId, request);

      logger.info("Game with ID {} updated successfully", gameId);

      UpdateGameResponse response =
          new UpdateGameResponse(
              game.getName(),
              game.getDescription(),
              game.getGamePhoto(),
              game.getMaxPlayers(),
              game.getIsPublic(),
              game.getTheme() == null ? null : game.getTheme().getId());
      logger.info("Game updated response: {}", response);
      return ResponseEntity.ok(response);
    } catch (HandledException e) {
      logger.error("Handled exception while update game: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(null);
    } catch (Exception e) {
      logger.error("Error updating game: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Endpoint to delete a game by its ID.
   *
   * <p>This method handles HTTP POST requests to delete a game. It verifies if the authenticated
   * user has admin privileges for the specified game before proceeding with the deletion. If the
   * user is not authorized, a 403 Forbidden response is returned. In case of any errors during the
   * process, a 500 Internal Server Error response is sent.
   *
   * @param jwt The JWT token of the authenticated user, used to extract claims.
   * @param gameId The ID of the game to be deleted, provided as a path variable.
   * @return A ResponseEntity indicating the result of the operation: - 204 No Content if the game
   *     is successfully deleted. - 400 Bad Request if the request is invalid or handled exception
   *     occurs. - 403 Forbidden if the user is not authorized. - 404 Not Found if the game does not
   *     exist. - 500 Internal Server Error if an exception occurs.
   */
  @Operation(
      summary = "Delete a game by ID",
      description =
          "Deletes a game by its ID. The user must have admin privileges for the game to perform the deletion.",
      operationId = "deleteGame")
  @PostMapping("/{gameId}/delete")
  public ResponseEntity<Void> deleteGame(
      @AuthenticationPrincipal Jwt jwt, @PathVariable Long gameId) {
    try {
      logger.info("Deleting game with ID: {}", gameId);
      // Check if the user has admin privileges for the game
      if (!subscriptionService.isAdmin(jwt.getClaim(AUTH0_AUDIENCE_EMAIL).toString(), gameId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
      // Perform the game deletion
      gameService.deleteGame(gameId);

      logger.info("Game with ID {} deleted successfully", gameId);
      return ResponseEntity.noContent().build();

    } catch (HandledException e) {
      logger.error("Handled exception while delete game: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(null);
    } catch (Exception e) {
      logger.error("Error deleting game: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
