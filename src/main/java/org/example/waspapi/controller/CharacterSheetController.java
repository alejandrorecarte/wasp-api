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
import org.example.waspapi.dto.requests.character.CreateCharacterSheetRequest;
import org.example.waspapi.dto.responses.character.GetCharacterSheetResponse;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.CharacterSheet;
import org.example.waspapi.service.CharacterSheetService;
import org.example.waspapi.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(
    name = "Character Sheets",
    description = "Endpoints for managing character sheets within a game")
@RequestMapping("/games/{gameId}/characters")
public class CharacterSheetController {

  private static final Logger logger = LoggerFactory.getLogger(CharacterSheetController.class);
  private final CharacterSheetService characterSheetService;
  private final SubscriptionService subscriptionService;

  public CharacterSheetController(
      CharacterSheetService characterSheetService, SubscriptionService subscriptionService) {
    this.characterSheetService = characterSheetService;
    this.subscriptionService = subscriptionService;
  }

  @Operation(
      summary = "Create a character sheet",
      description =
          "Creates a new character sheet for the authenticated user in the specified game. Requires subscription.",
      operationId = "createCharacterSheet")
  @PostMapping
  public ResponseEntity<GetCharacterSheetResponse> createCharacterSheet(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @Valid @RequestBody CreateCharacterSheetRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} creating character sheet for game {}", userId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    CharacterSheet sheet = characterSheetService.create(gameId, userId, request);
    return ResponseEntity.ok(toResponse(sheet));
  }

  @Operation(
      summary = "Update a character sheet",
      description = "Updates an existing character sheet. Only the owner can update it.",
      operationId = "updateCharacterSheet")
  @PutMapping("/{characterId}")
  public ResponseEntity<GetCharacterSheetResponse> updateCharacterSheet(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID characterId,
      @Valid @RequestBody CreateCharacterSheetRequest request) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} updating character sheet {} in game {}", userId, characterId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    CharacterSheet sheet = characterSheetService.update(characterId, userId, request);
    return ResponseEntity.ok(toResponse(sheet));
  }

  @Operation(
      summary = "Delete a character sheet",
      description = "Deletes a character sheet. Only the owner or a game admin can delete it.",
      operationId = "deleteCharacterSheet")
  @DeleteMapping("/{characterId}")
  public ResponseEntity<Void> deleteCharacterSheet(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID characterId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} deleting character sheet {} in game {}", userId, characterId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    characterSheetService.delete(characterId, userId, gameId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "List all character sheets of a game",
      description = "Returns all character sheets for the specified game. Requires subscription.",
      operationId = "getCharacterSheetsByGame")
  @GetMapping
  public ResponseEntity<List<GetCharacterSheetResponse>> getCharacterSheetsByGame(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} listing character sheets for game {}", userId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<GetCharacterSheetResponse> sheets =
        characterSheetService.getByGameId(gameId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    return ResponseEntity.ok(sheets);
  }

  @Operation(
      summary = "List my character sheets",
      description =
          "Returns the authenticated user's character sheets for the specified game. Requires subscription.",
      operationId = "getMyCharacterSheets")
  @GetMapping("/mine")
  public ResponseEntity<List<GetCharacterSheetResponse>> getMyCharacterSheets(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID gameId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} listing own character sheets for game {}", userId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<GetCharacterSheetResponse> sheets =
        characterSheetService.getByGameIdAndUserId(gameId, userId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    return ResponseEntity.ok(sheets);
  }

  @Operation(
      summary = "Get a character sheet by ID",
      description = "Returns a single character sheet. Requires subscription to the game.",
      operationId = "getCharacterSheet")
  @GetMapping("/{characterId}")
  public ResponseEntity<GetCharacterSheetResponse> getCharacterSheet(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID characterId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info("User {} fetching character sheet {} in game {}", userId, characterId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    CharacterSheet sheet = characterSheetService.getById(characterId);
    return ResponseEntity.ok(toResponse(sheet));
  }

  @Operation(
      summary = "Upload character photo",
      description = "Uploads a photo for a character sheet. Only the owner can upload.",
      operationId = "uploadCharacterPhoto")
  @PostMapping(value = "/{characterId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<GetCharacterSheetResponse> uploadCharacterPhoto(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID characterId,
      @RequestParam("file") MultipartFile file)
      throws IOException {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info(
        "User {} uploading photo for character sheet {} in game {}", userId, characterId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new HandledException(INVALID_FILE_TYPE, HttpStatus.BAD_REQUEST);
    }

    CharacterSheet sheet =
        characterSheetService.uploadPhoto(characterId, userId, file.getBytes(), contentType);
    return ResponseEntity.ok(toResponse(sheet));
  }

  @Operation(
      summary = "Delete character photo",
      description = "Deletes the photo of a character sheet. Only the owner can delete.",
      operationId = "deleteCharacterPhoto")
  @DeleteMapping("/{characterId}/photo")
  public ResponseEntity<GetCharacterSheetResponse> deleteCharacterPhoto(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID gameId,
      @PathVariable UUID characterId) {
    UUID userId = UUID.fromString(jwt.getSubject());
    logger.info(
        "User {} deleting photo for character sheet {} in game {}", userId, characterId, gameId);
    if (!subscriptionService.isSubscribed(userId, gameId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    CharacterSheet sheet = characterSheetService.deletePhoto(characterId, userId);
    return ResponseEntity.ok(toResponse(sheet));
  }

  private GetCharacterSheetResponse toResponse(CharacterSheet sheet) {
    return new GetCharacterSheetResponse(
        sheet.getId(),
        sheet.getName(),
        sheet.getRole(),
        sheet.getDescription(),
        sheet.getLevel(),
        characterSheetService.resolvePhotoUrl(sheet.getCharacterPhoto()),
        sheet.getCreatedAt(),
        sheet.getUser().getId(),
        sheet.getUser().getNickname());
  }
}
