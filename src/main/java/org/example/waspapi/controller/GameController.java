package org.example.waspapi.controller;

import jakarta.validation.Valid;
import org.example.waspapi.dto.requests.game.CreateGameRequest;
import org.example.waspapi.model.Game;
import org.example.waspapi.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@Valid @RequestBody CreateGameRequest request) {
        Game game = gameService.createGame(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }
}
