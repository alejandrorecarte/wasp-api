package org.example.waspapi.controller;

import static org.example.waspapi.Constants.INVALID_FILE_TYPE;
import static org.example.waspapi.Constants.PHOTO_UPLOAD_FAILED;
import static org.example.waspapi.Constants.THEME_NOT_FOUND;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.waspapi.dto.responses.theme.GetThemeResponse;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Theme;
import org.example.waspapi.repository.ThemeRepository;
import org.example.waspapi.service.SupabaseStorageService;
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
@Tag(name = "Themes", description = "Endpoints for managing themes")
@RequestMapping("/themes")
public class ThemeController {

  private static final Logger logger = LoggerFactory.getLogger(ThemeController.class);
  private static final String THEME_PHOTOS_BUCKET = "theme-photos";

  private final ThemeRepository themeRepository;
  private final SupabaseStorageService storageService;

  public ThemeController(ThemeRepository themeRepository, SupabaseStorageService storageService) {
    this.themeRepository = themeRepository;
    this.storageService = storageService;
  }

  @Operation(
      summary = "Search themes by name",
      description =
          "Returns all themes matching the search term (case-insensitive partial match). "
              + "If no search term is provided, returns all themes.",
      operationId = "searchThemes")
  @GetMapping
  public ResponseEntity<List<GetThemeResponse>> searchThemes(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) String name) {
    logger.info("Searching themes with name filter: {}", name);
    List<Theme> themes;
    if (name == null || name.trim().isEmpty()) {
      themes = themeRepository.findAll();
    } else {
      themes = themeRepository.findByNameContainingIgnoreCase(name.trim());
    }
    List<GetThemeResponse> response =
        themes.stream().map(this::toThemeResponse).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  private GetThemeResponse toThemeResponse(Theme theme) {
    String photoUrl = null;
    if (theme.getThemePhoto() != null) {
      photoUrl = storageService.getPublicUrl(THEME_PHOTOS_BUCKET, theme.getThemePhoto());
    }
    return new GetThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), photoUrl);
  }

  @Operation(
      summary = "Upload a theme photo",
      description = "Uploads a photo for a theme. Accepts multipart file upload.",
      operationId = "uploadThemePhoto")
  @PostMapping(value = "/{themeId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadThemePhoto(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID themeId,
      @RequestParam("file") MultipartFile file)
      throws IOException {
    logger.info("Uploading photo for theme: {}", themeId);

    Theme theme =
        themeRepository
            .findById(themeId)
            .orElseThrow(() -> new HandledException(THEME_NOT_FOUND, HttpStatus.NOT_FOUND));

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new HandledException(INVALID_FILE_TYPE, HttpStatus.BAD_REQUEST);
    }

    String path = themeId.toString();
    try {
      storageService.upload(THEME_PHOTOS_BUCKET, path, file.getBytes(), contentType);
    } catch (Exception e) {
      logger.error("Failed to upload photo for theme {}: {}", themeId, e.getMessage());
      throw new HandledException(PHOTO_UPLOAD_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    theme.setThemePhoto(path);
    themeRepository.save(theme);
    logger.info("Photo uploaded for theme: {}", themeId);

    return ResponseEntity.ok(storageService.getPublicUrl(THEME_PHOTOS_BUCKET, path));
  }

  @Operation(
      summary = "Delete a theme photo",
      description = "Deletes the photo of a theme.",
      operationId = "deleteThemePhoto")
  @DeleteMapping("/{themeId}/photo")
  public ResponseEntity<Void> deleteThemePhoto(
      @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @PathVariable UUID themeId) {
    logger.info("Deleting photo for theme: {}", themeId);

    Theme theme =
        themeRepository
            .findById(themeId)
            .orElseThrow(() -> new HandledException(THEME_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (theme.getThemePhoto() != null) {
      try {
        storageService.delete(THEME_PHOTOS_BUCKET, theme.getThemePhoto());
      } catch (Exception e) {
        logger.error("Failed to delete photo for theme {}: {}", themeId, e.getMessage());
      }
      theme.setThemePhoto(null);
      themeRepository.save(theme);
      logger.info("Photo deleted for theme: {}", themeId);
    }

    return ResponseEntity.noContent().build();
  }
}
