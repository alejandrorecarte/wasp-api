package org.example.waspapi.service;

import static org.example.waspapi.Constants.CHARACTER_SHEET_NOT_FOUND;
import static org.example.waspapi.Constants.GAME_NOT_FOUND;
import static org.example.waspapi.Constants.PHOTO_UPLOAD_FAILED;
import static org.example.waspapi.Constants.USER_NOT_FOUND;

import java.util.List;
import java.util.UUID;
import org.example.waspapi.dto.requests.character.CreateCharacterSheetRequest;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.CharacterSheet;
import org.example.waspapi.model.Game;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.CharacterSheetRepository;
import org.example.waspapi.repository.GameRepository;
import org.example.waspapi.repository.SubscriptionRepository;
import org.example.waspapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CharacterSheetService {

  private static final Logger logger = LoggerFactory.getLogger(CharacterSheetService.class);
  private static final String CHARACTER_PHOTOS_BUCKET = "character-photos";

  private final CharacterSheetRepository characterSheetRepository;
  private final UserRepository userRepository;
  private final GameRepository gameRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final SupabaseStorageService storageService;

  public CharacterSheetService(
      CharacterSheetRepository characterSheetRepository,
      UserRepository userRepository,
      GameRepository gameRepository,
      SubscriptionRepository subscriptionRepository,
      SupabaseStorageService storageService) {
    this.characterSheetRepository = characterSheetRepository;
    this.userRepository = userRepository;
    this.gameRepository = gameRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.storageService = storageService;
  }

  public CharacterSheet create(UUID gameId, UUID userId, CreateCharacterSheetRequest request) {
    logger.debug("User {} creating character sheet for game {}", userId, gameId);
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

    CharacterSheet sheet = new CharacterSheet();
    sheet.setGame(game);
    sheet.setUser(user);
    sheet.setName(request.getName());
    sheet.setRole(request.getRole());
    sheet.setDescription(request.getDescription());
    sheet.setLevel(request.getLevel());

    CharacterSheet saved = characterSheetRepository.save(sheet);
    logger.info("Character sheet created with ID: {}", saved.getId());
    return saved;
  }

  public CharacterSheet update(
      UUID characterSheetId, UUID userId, CreateCharacterSheetRequest request) {
    logger.debug("User {} updating character sheet {}", userId, characterSheetId);
    CharacterSheet sheet =
        characterSheetRepository
            .findById(characterSheetId)
            .orElseThrow(
                () -> new HandledException(CHARACTER_SHEET_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (!sheet.getUser().getId().equals(userId)) {
      throw new HandledException(
          "Only the owner can update this character sheet", HttpStatus.FORBIDDEN);
    }

    if (request.getName() != null) {
      sheet.setName(request.getName());
    }
    if (request.getRole() != null) {
      sheet.setRole(request.getRole());
    }
    if (request.getDescription() != null) {
      sheet.setDescription(request.getDescription());
    }
    if (request.getLevel() != null) {
      sheet.setLevel(request.getLevel());
    }

    CharacterSheet saved = characterSheetRepository.save(sheet);
    logger.info("Character sheet updated: {}", characterSheetId);
    return saved;
  }

  public void delete(UUID characterSheetId, UUID userId, UUID gameId) {
    logger.debug("User {} deleting character sheet {}", userId, characterSheetId);
    CharacterSheet sheet =
        characterSheetRepository
            .findById(characterSheetId)
            .orElseThrow(
                () -> new HandledException(CHARACTER_SHEET_NOT_FOUND, HttpStatus.NOT_FOUND));

    boolean isOwner = sheet.getUser().getId().equals(userId);
    boolean isAdmin =
        subscriptionRepository.existsByUserIdAndGameIdAndIsAdminTrueAndIsActiveTrue(userId, gameId);

    if (!isOwner && !isAdmin) {
      throw new HandledException(
          "Only the owner or a game admin can delete this character sheet", HttpStatus.FORBIDDEN);
    }

    if (sheet.getCharacterPhoto() != null) {
      try {
        storageService.delete(CHARACTER_PHOTOS_BUCKET, sheet.getCharacterPhoto());
      } catch (Exception e) {
        logger.warn("Failed to delete character photo: {}", e.getMessage());
      }
    }

    characterSheetRepository.delete(sheet);
    logger.info("Character sheet deleted: {}", characterSheetId);
  }

  public List<CharacterSheet> getByGameId(UUID gameId) {
    logger.debug("Fetching character sheets for game: {}", gameId);
    return characterSheetRepository.findByGameIdOrderByCreatedAtDesc(gameId);
  }

  public CharacterSheet getById(UUID characterSheetId) {
    logger.debug("Fetching character sheet: {}", characterSheetId);
    return characterSheetRepository
        .findById(characterSheetId)
        .orElseThrow(() -> new HandledException(CHARACTER_SHEET_NOT_FOUND, HttpStatus.NOT_FOUND));
  }

  public List<CharacterSheet> getByGameIdAndUserId(UUID gameId, UUID userId) {
    logger.debug("Fetching character sheets for user {} in game {}", userId, gameId);
    return characterSheetRepository.findByGameIdAndUserId(gameId, userId);
  }

  public CharacterSheet uploadPhoto(
      UUID characterSheetId, UUID userId, byte[] data, String contentType) {
    logger.debug("User {} uploading photo for character sheet {}", userId, characterSheetId);
    CharacterSheet sheet =
        characterSheetRepository
            .findById(characterSheetId)
            .orElseThrow(
                () -> new HandledException(CHARACTER_SHEET_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (!sheet.getUser().getId().equals(userId)) {
      throw new HandledException(
          "Only the owner can upload a photo for this character sheet", HttpStatus.FORBIDDEN);
    }

    String path = sheet.getGame().getId() + "/" + characterSheetId;
    try {
      storageService.upload(CHARACTER_PHOTOS_BUCKET, path, data, contentType);
    } catch (Exception e) {
      logger.error("Failed to upload character photo: {}", e.getMessage());
      throw new HandledException(PHOTO_UPLOAD_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    sheet.setCharacterPhoto(path);
    CharacterSheet saved = characterSheetRepository.save(sheet);
    logger.info("Character photo uploaded for sheet: {}", characterSheetId);
    return saved;
  }

  public CharacterSheet deletePhoto(UUID characterSheetId, UUID userId) {
    logger.debug("User {} deleting photo for character sheet {}", userId, characterSheetId);
    CharacterSheet sheet =
        characterSheetRepository
            .findById(characterSheetId)
            .orElseThrow(
                () -> new HandledException(CHARACTER_SHEET_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (!sheet.getUser().getId().equals(userId)) {
      throw new HandledException(
          "Only the owner can delete the photo for this character sheet", HttpStatus.FORBIDDEN);
    }

    if (sheet.getCharacterPhoto() != null) {
      try {
        storageService.delete(CHARACTER_PHOTOS_BUCKET, sheet.getCharacterPhoto());
      } catch (Exception e) {
        logger.warn("Failed to delete character photo from storage: {}", e.getMessage());
      }
      sheet.setCharacterPhoto(null);
      characterSheetRepository.save(sheet);
    }

    logger.info("Character photo deleted for sheet: {}", characterSheetId);
    return sheet;
  }

  public String resolvePhotoUrl(String photoPath) {
    if (photoPath == null) {
      return null;
    }
    return storageService.getPublicUrl(CHARACTER_PHOTOS_BUCKET, photoPath);
  }
}
