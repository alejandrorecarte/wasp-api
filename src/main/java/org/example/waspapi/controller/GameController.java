package org.example.waspapi.controller;

import static org.example.waspapi.Constants.INVALID_FILE_TYPE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.example.waspapi.dto.requests.game.CreateGameRequest;
import org.example.waspapi.dto.requests.game.UpdateGameRequest;
import org.example.waspapi.dto.requests.subscription.CreateSubscriptionRequest;
import org.example.waspapi.dto.responses.game.GetGameResponse;
import org.example.waspapi.dto.responses.game.UpdateGameResponse;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Game;
import org.example.waspapi.model.Subscription;
import org.example.waspapi.service.GameService;
import org.example.waspapi.service.SubscriptionService;
import org.example.waspapi.service.SupabaseStorageService;
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
@Tag(name = "Games", description = "Endpoints for managing games")
@RequestMapping("/games")
public class GameController {

  private static final Logger logger = LoggerFactory.getLogger(GameController.class);
  private final GameService gameService;
  private final SubscriptionService subscriptionService;
  private final SupabaseStorageService storageService;

  public GameController(
      GameService gameService,
      SubscriptionService subscriptionService,
      SupabaseStorageService storageService) {
    this.gameService = gameService;
    this.subscriptionService = subscriptionService;
    this.storageService = storageService;
  }

  @Operation(
      summary = "Get games by authenticated user",
      description =
          "Returns all games the authenticated user is subscribed to, excluding deleted games.",
      operationId = "getMyGames")
  @GetMapping("/me")
  public ResponseEntity<List<GetGameResponse>> getMyGames(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("Fetching games for user: {}", userId);
    List<GetGameResponse> games =
        subscriptionService.getGamesByUserId(userId).stream()
            .map(
                game ->
                    new GetGameResponse(
                        game.getId(),
                        game.getName(),
                        game.getDescription(),
                        resolvePhoto(game),
                        game.getMaxPlayers(),
                        game.getIsPublic(),
                        game.getTheme() == null ? null : game.getTheme().getName(),
                        game.getMasterUser() == null ? null : game.getMasterUser().getId(),
                        subscriptionService.countPlayersByGameId(game.getId()),
                        null))
            .collect(Collectors.toList());
    return ResponseEntity.ok(games);
  }

  @Operation(
      summary = "Get public games",
      description =
          "Returns all public games that are not deleted, with pagination support. "
              + "Optionally filter by game name and/or theme name (case-insensitive partial match).",
      operationId = "getPublicGames")
  @GetMapping("/public")
  public ResponseEntity<Page<GetGameResponse>> getPublicGames(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String themeName,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    logger.info(
        "Fetching public games - name: {}, themeName: {}, page: {}, size: {}",
        name,
        themeName,
        page,
        size);
    Page<GetGameResponse> games =
        gameService
            .getPublicGames(name, themeName, PageRequest.of(page, size))
            .map(
                game ->
                    new GetGameResponse(
                        game.getId(),
                        game.getName(),
                        game.getDescription(),
                        resolvePhoto(game),
                        game.getMaxPlayers(),
                        game.getIsPublic(),
                        game.getTheme() == null ? null : game.getTheme().getName(),
                        game.getMasterUser() == null ? null : game.getMasterUser().getId(),
                        subscriptionService.countPlayersByGameId(game.getId()),
                        null));
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
    UUID userId = UUID.fromString(jwt.getSubject());
    Game game = gameService.createGame(request, userId);

    CreateSubscriptionRequest subscription =
        new CreateSubscriptionRequest(userId, game.getId(), request.getName(), "OWNER", true);
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
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    logger.info("Fetching game with ID: {}", gameId);
    UUID userId = UUID.fromString(jwt.getSubject());
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Game game = gameService.getGameById(gameId);
    if (game.getIsDeleted()) {
      return ResponseEntity.notFound().build();
    }
    logger.info("Game with ID {} fetched successfully", gameId);

    List<GetGameResponse.PlayerInfo> players =
        subscriptionService.getSubscriptionsByGameId(gameId).stream()
            .map(this::toPlayerInfo)
            .collect(Collectors.toList());

    GetGameResponse response =
        new GetGameResponse(
            game.getId(),
            game.getName(),
            game.getDescription(),
            resolvePhoto(game),
            game.getMaxPlayers(),
            game.getIsPublic(),
            game.getTheme() == null ? null : game.getTheme().getName(),
            game.getMasterUser() == null ? null : game.getMasterUser().getId(),
            subscriptionService.countPlayersByGameId(gameId),
            players);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Get players of a game",
      description =
          "Returns all users subscribed to the specified game. The user must be subscribed to the game.",
      operationId = "getGamePlayers")
  @GetMapping("/{gameId}/players")
  public ResponseEntity<List<GetGameResponse.PlayerInfo>> getGamePlayers(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("Fetching players for game: {}", gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<GetGameResponse.PlayerInfo> players =
        subscriptionService.getSubscriptionsByGameId(gameId).stream()
            .map(this::toPlayerInfo)
            .collect(Collectors.toList());
    return ResponseEntity.ok(players);
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
    try {
      logger.info("Updating game with ID: {} and request: {}", gameId, request);
      String emailClaim = jwt.getClaim(AUTH0_AUDIENCE_EMAIL).toString();
      if (!subscriptionService.isAdmin(emailClaim, gameId)) {
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
    Game game = gameService.updateGame(gameId, request);

    logger.info("Game with ID {} updated successfully", gameId);

    UpdateGameResponse response =
        new UpdateGameResponse(
            game.getName(),
            game.getDescription(),
            resolvePhoto(game),
            game.getMaxPlayers(),
            game.getIsPublic(),
            game.getTheme() == null ? null : game.getTheme().getName(),
            game.getMasterUser() == null ? null : game.getMasterUser().getId(),
            subscriptionService.countPlayersByGameId(gameId));
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Delete a game by ID",
      description =
          "Deletes a game by its ID. The user must have admin privileges for the game to perform the deletion.",
      operationId = "deleteGame")
  @DeleteMapping("/{gameId}")
  public ResponseEntity<Void> deleteGame(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    logger.info("Deleting game with ID: {}", gameId);
    UUID userId = UUID.fromString(jwt.getSubject());
    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    gameService.deleteGame(gameId);

    logger.info("Game with ID {} deleted successfully", gameId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Upload a game photo",
      description =
          "Uploads a photo for a game. The user must have admin privileges. Accepts multipart file upload.",
      operationId = "uploadGamePhoto")
  @PostMapping(value = "/{gameId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadGamePhoto(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @RequestParam("file") MultipartFile file)
      throws IOException {
    logger.info("Uploading photo for game: {}", gameId);
    UUID userId = UUID.fromString(jwt.getSubject());
    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new HandledException(INVALID_FILE_TYPE, HttpStatus.BAD_REQUEST);
    }

    String publicUrl = gameService.uploadPhoto(gameId, file.getBytes(), contentType);
    return ResponseEntity.ok(publicUrl);
  }

  @Operation(
      summary = "Delete a game photo",
      description =
          "Deletes the photo of a game. The user must have admin privileges for the game.",
      operationId = "deleteGamePhoto")
  @DeleteMapping("/{gameId}/photo")
  public ResponseEntity<Void> deleteGamePhoto(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    logger.info("Deleting photo for game: {}", gameId);
    UUID userId = UUID.fromString(jwt.getSubject());
    if (!subscriptionService.isAdmin(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    gameService.deletePhoto(gameId);
    return ResponseEntity.noContent().build();
  }

  private String resolvePhoto(Game game) {
    if (game.getGamePhoto() != null) {
      return storageService.getPublicUrl("game-photos", game.getGamePhoto());
    }
    if (game.getTheme() != null && game.getTheme().getThemePhoto() != null) {
      return storageService.getPublicUrl("theme-photos", game.getTheme().getThemePhoto());
    }
    return null;
  }

  private GetGameResponse.PlayerInfo toPlayerInfo(Subscription subscription) {
    return new GetGameResponse.PlayerInfo(
        subscription.getUser().getId(),
        subscription.getUser().getNickname(),
        subscription.getRole(),
        subscription.getUser().getProfilePhoto());
  }
}
