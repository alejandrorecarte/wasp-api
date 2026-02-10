package org.example.waspapi.controller;

import static org.example.waspapi.Constants.SUPABASE_EMAIL_CLAIM;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.example.waspapi.dto.requests.game.CreateGameRequest;
import org.example.waspapi.dto.requests.game.UpdateGameRequest;
import org.example.waspapi.dto.requests.subscription.CreateSubscriptionRequest;
import org.example.waspapi.dto.responses.game.GetGameResponse;
import org.example.waspapi.dto.responses.game.UpdateGameResponse;
import org.example.waspapi.model.Game;
import org.example.waspapi.service.GameService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

  @Operation(
      summary = "Get games by authenticated user",
      description =
          "Returns all games the authenticated user is subscribed to, excluding deleted games.",
      operationId = "getMyGames")
  @GetMapping("/me")
  public ResponseEntity<List<GetGameResponse>> getMyGames(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    String email = jwt.getClaim(SUPABASE_EMAIL_CLAIM).toString();
    logger.info("Fetching games for user: {}", email);
    List<GetGameResponse> games =
        subscriptionService.getGamesByUserEmail(email).stream()
            .map(
                game ->
                    new GetGameResponse(
                        game.getName(),
                        game.getDescription(),
                        game.getGamePhoto(),
                        game.getMaxPlayers(),
                        game.getIsPublic(),
                        game.getTheme() == null ? null : game.getTheme().getId()))
            .collect(Collectors.toList());
    return ResponseEntity.ok(games);
  }

  @Operation(
      summary = "Get public games",
      description =
          "Returns all public games that are not deleted, with pagination support.",
      operationId = "getPublicGames")
  @GetMapping("/public")
  public ResponseEntity<Page<GetGameResponse>> getPublicGames(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    logger.info("Fetching public games - page: {}, size: {}", page, size);
    Page<GetGameResponse> games =
        gameService
            .getPublicGames(PageRequest.of(page, size))
            .map(
                game ->
                    new GetGameResponse(
                        game.getName(),
                        game.getDescription(),
                        game.getGamePhoto(),
                        game.getMaxPlayers(),
                        game.getIsPublic(),
                        game.getTheme() == null ? null : game.getTheme().getId()));
    return ResponseEntity.ok(games);
  }

  @Operation(
      summary = "Create a new game",
      description =
          "Creates a new game and associates it with the authenticated user as the owner. "
              + "A subscription is also created for the user.",
      operationId = "createGame")
  @PostMapping("/create")
  public ResponseEntity<UUID> createGame(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CreateGameRequest request) {
    logger.info("Creating game with request: {}", request);
    String auth0Email = jwt.getClaim(SUPABASE_EMAIL_CLAIM).toString();
    Game game = gameService.createGame(request);

    CreateSubscriptionRequest subscription =
        new CreateSubscriptionRequest(auth0Email, game.getId(), request.getName(), "OWNER", true);
    subscriptionService.createSubscription(subscription);

    logger.info("Game created successfully with ID: {}", game.getId());
    return ResponseEntity.ok(game.getId());
  }

  @Operation(
      summary = "Get a game by ID",
      description =
          "Fetches a game by its ID. The user must be subscribed to the game to access its details.",
      operationId = "getGame")
  @GetMapping("/{gameId}")
  public ResponseEntity<GetGameResponse> getGame(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId) {
    logger.info("Fetching game with ID: {}", gameId);
    String auth0Email = jwt.getClaim(SUPABASE_EMAIL_CLAIM).toString();
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
  }

  @Operation(
      summary = "Update a game by ID",
      description =
          "Updates a game by its ID. The user must have admin privileges for the game to perform the update.",
      operationId = "updateGame")
  @PutMapping("/{gameId}/update")
  public ResponseEntity<UpdateGameResponse> updateGame(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @Valid @RequestBody UpdateGameRequest request) {
    logger.info("Updating game with ID: {}", gameId);
    if (!subscriptionService.isAdmin(jwt.getClaim(SUPABASE_EMAIL_CLAIM).toString(), gameId)) {
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
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Delete a game by ID",
      description =
          "Deletes a game by its ID. The user must have admin privileges for the game to perform the deletion.",
      operationId = "deleteGame")
  @PostMapping("/{gameId}/delete")
  public ResponseEntity<Void> deleteGame(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId) {
    logger.info("Deleting game with ID: {}", gameId);
    if (!subscriptionService.isAdmin(jwt.getClaim(SUPABASE_EMAIL_CLAIM).toString(), gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    gameService.deleteGame(gameId);

    logger.info("Game with ID {} deleted successfully", gameId);
    return ResponseEntity.noContent().build();
  }
}
