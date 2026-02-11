package org.example.waspapi.service;

import static org.example.waspapi.Constants.GAME_NOT_FOUND;
import static org.example.waspapi.Constants.PHOTO_UPLOAD_FAILED;
import static org.example.waspapi.Constants.THEME_NOT_FOUND;
import static org.example.waspapi.Constants.USER_NOT_FOUND;

import java.util.UUID;
import org.example.waspapi.dto.requests.game.CreateGameRequest;
import org.example.waspapi.dto.requests.game.UpdateGameRequest;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Game;
import org.example.waspapi.model.Theme;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.GameRepository;
import org.example.waspapi.repository.ThemeRepository;
import org.example.waspapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private static final Logger logger = LoggerFactory.getLogger(GameService.class);
  private final GameRepository gameRepository;
  private final ThemeRepository themeRepository;
  private final UserRepository userRepository;
  private final SupabaseStorageService storageService;

  private static final String GAME_PHOTOS_BUCKET = "game-photos";

  public GameService(
      GameRepository gameRepository,
      ThemeRepository themeRepository,
      UserRepository userRepository,
      SupabaseStorageService storageService) {
    this.gameRepository = gameRepository;
    this.themeRepository = themeRepository;
    this.userRepository = userRepository;
    this.storageService = storageService;
  }

  public Game createGame(CreateGameRequest request, UUID masterUserId) {
    logger.debug("Creating game with name: {}", request.getName());
    Game game = new Game();
    game.setName(request.getName());
    game.setDescription(request.getDescription());
    game.setMaxPlayers(request.getMaxPlayers());
    game.setIsPublic(request.getPublic());
    game.setIsDeleted(false);

    User masterUser =
        userRepository
            .findById(masterUserId)
            .orElseThrow(
                () -> {
                  logger.warn("Master user not found: {}", masterUserId);
                  return new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    game.setMasterUser(masterUser);

    if (request.getThemeId() != null) {
      Theme theme =
          themeRepository
              .findById(request.getThemeId())
              .orElseThrow(
                  () -> {
                    logger.warn(
                        "Theme not found while creating a new game: {}", request.getThemeId());
                    return new HandledException(THEME_NOT_FOUND, HttpStatus.NOT_FOUND);
                  });
      game.setTheme(theme);
    }

    logger.debug("Game created with ID: {}", game.getId());
    return gameRepository.save(game);
  }

  public Game updateGame(UUID gameId, UpdateGameRequest request) {
    logger.debug("Updating game with ID: {}", gameId);
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (request.getName() != null) {
      game.setName(request.getName());
    }
    if (request.getDescription() != null) {
      game.setDescription(request.getDescription());
    }
    if (request.getMaxPlayers() != null) {
      game.setMaxPlayers(request.getMaxPlayers());
    }
    if (request.getIsPublic() != null) {
      game.setIsPublic(request.getIsPublic());
    }

    if (request.getThemeId() != null) {
      Theme theme =
          themeRepository
              .findById(request.getThemeId())
              .orElseThrow(
                  () -> {
                    logger.warn("Theme not found while updating game with ID: {}", gameId);
                    return new HandledException(THEME_NOT_FOUND, HttpStatus.NOT_FOUND);
                  });
      game.setTheme(theme);
    }

    logger.debug("Game updated with ID: {}", game.getId());
    return gameRepository.save(game);
  }

  public Game getGameById(UUID gameId) {
    logger.debug("Fetching game by id: {}", gameId);
    return gameRepository
        .findById(gameId)
        .orElseThrow(
            () -> {
              logger.warn("Game not found: {}", gameId);
              return new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
            });
  }

  public void deleteGame(UUID gameId) {
    logger.debug("Deleting game with ID: {}", gameId);
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(
                () -> {
                  logger.warn("Game not found: {}", gameId);
                  return new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    game.setIsDeleted(true);
    gameRepository.save(game);
    logger.info("Game marked as deleted: {}", gameId);
  }

  public Page<Game> getPublicGames(String name, String themeName, Pageable pageable) {
    return gameRepository.findPublicGamesWithFilters(name, themeName, pageable);
  }

  public String uploadPhoto(UUID gameId, byte[] data, String contentType) {
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));

    String path = gameId.toString();
    try {
      storageService.upload(GAME_PHOTOS_BUCKET, path, data, contentType);
    } catch (Exception e) {
      logger.error("Failed to upload photo for game {}: {}", gameId, e.getMessage());
      throw new HandledException(PHOTO_UPLOAD_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    game.setGamePhoto(path);
    gameRepository.save(game);
    logger.info("Photo uploaded for game: {}", gameId);
    return storageService.getPublicUrl(GAME_PHOTOS_BUCKET, path);
  }

  public void deletePhoto(UUID gameId) {
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (game.getGamePhoto() != null) {
      try {
        storageService.delete(GAME_PHOTOS_BUCKET, game.getGamePhoto());
      } catch (Exception e) {
        logger.error("Failed to delete photo for game {}: {}", gameId, e.getMessage());
      }
      game.setGamePhoto(null);
      gameRepository.save(game);
      logger.info("Photo deleted for game: {}", gameId);
    }
  }
}
