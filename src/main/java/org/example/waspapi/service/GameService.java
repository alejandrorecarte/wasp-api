package org.example.waspapi.service;

import org.example.waspapi.dto.requests.game.CreateGameRequest;
import org.example.waspapi.model.Game;
import org.example.waspapi.model.Theme;
import org.example.waspapi.repository.GameRepository;
import org.example.waspapi.repository.ThemeRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ThemeRepository themeRepository; // para buscar el tema si lo tienes

    public GameService(GameRepository gameRepository, ThemeRepository themeRepository) {
        this.gameRepository = gameRepository;
        this.themeRepository = themeRepository;
    }

    public Game createGame(CreateGameRequest request) {
        Game game = new Game();
        game.setName(request.getName());
        game.setDescription(request.getDescription());
        game.setGamePhoto(request.getGamePhoto());
        game.setMaxPlayers(request.getMaxPlayers());
        game.setIsPublic(request.getPublic());

        if (request.getThemeId() != null) {
            Theme theme = themeRepository.findById(request.getThemeId())
                    .orElseThrow(() -> new RuntimeException("Tema no encontrado"));
            game.setTheme(theme);
        }

        return gameRepository.save(game);
    }
}