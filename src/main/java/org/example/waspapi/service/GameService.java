package org.example.waspapi.service;

import static org.example.waspapi.Constants.GAME_NOT_FOUND;
import static org.example.waspapi.Constants.THEME_NOT_FOUND;

import org.example.waspapi.dto.requests.game.CreateGameRequest;
import org.example.waspapi.dto.requests.game.UpdateGameRequest;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Game;
import org.example.waspapi.model.Theme;
import org.example.waspapi.repository.GameRepository;
import org.example.waspapi.repository.ThemeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private static final Logger logger = LoggerFactory.getLogger(GameService.class);
  private final GameRepository gameRepository;
  private final ThemeRepository themeRepository;

  public GameService(GameRepository gameRepository, ThemeRepository themeRepository) {
    this.gameRepository = gameRepository;
    this.themeRepository = themeRepository;
  }

  /**
   * Creates a new game in the system.
   *
   * <p>This method takes a request object containing the details of the game to be created, such as
   * name, description, game photo, maximum number of players, public visibility, and optionally an
   * associated theme. If a theme ID is provided, it validates that the theme exists. Throws an
   * exception if the theme is not found.
   *
   * @param request A CreateGameRequest object containing the details of the game to be created.
   * @return The Game object created and saved in the repository.
   * @throws HandledException If the specified theme is not found.
   */
  public Game createGame(CreateGameRequest request) {
    logger.debug("Creating game with name: {}", request.getName());
    Game game = new Game();
    game.setName(request.getName());
    game.setDescription(request.getDescription());
    game.setGamePhoto(request.getGamePhoto());
    game.setMaxPlayers(request.getMaxPlayers());
    game.setIsPublic(request.getPublic());
    game.setIsDeleted(false);

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

  /**
   * Updates an existing game in the system.
   *
   * <p>This method retrieves a game by its ID and updates its details based on the provided request
   * object. If the game is not found, it throws an exception. Optionally, if a theme ID is
   * provided, it validates that the theme exists and associates it with the game. Throws an
   * exception if the theme is not found.
   *
   * @param gameId The ID of the game to be updated.
   * @param request An UpdateGameRequest object containing the new details for the game.
   * @return The updated Game object saved in the repository.
   * @throws HandledException If the game or the specified theme is not found.
   */
  public Game updateGame(Long gameId, UpdateGameRequest request) {
    logger.debug("Updating game with ID: {}", gameId);
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));

    game.setName(request.getName());
    game.setDescription(request.getDescription());
    game.setGamePhoto(request.getGamePhoto());
    game.setMaxPlayers(request.getMaxPlayers());
    game.setIsPublic(request.getIsPublic());

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

  /**
   * Retrieves a game by its ID.
   *
   * <p>This method fetches a game from the repository using its unique identifier. If the game is
   * not found, it throws a HandledException with a 404 Not Found status.
   *
   * @param gameId The unique identifier of the game to retrieve.
   * @return The Game object corresponding to the provided ID.
   * @throws HandledException If the game is not found in the repository.
   */
  public Game getGameById(Long gameId) {
    logger.debug("Fetching game by id: {}", gameId);
    return gameRepository
        .findById(gameId)
        .orElseThrow(
            () -> {
              logger.warn("Game not found: {}", gameId);
              return new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND);
            });
  }

  /**
   * ted in the system.
   *
   * <p>Marks a game as dele This method retrieves a game by its ID and sets its "isDeleted"
   * property to true. If the game is not found, it throws a HandledException with a 404 Not Found
   * status.
   *
   * @param gameId The unique identifier of the game to delete.
   * @throws HandledException If the game is not found in the repository.
   */
  public void deleteGame(Long gameId) {
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
}
