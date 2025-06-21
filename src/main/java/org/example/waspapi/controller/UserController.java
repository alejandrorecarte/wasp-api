package org.example.waspapi.controller;

import static org.example.waspapi.Constants.AUTH0_AUDIENCE_EMAIL;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.waspapi.dto.requests.users.UpdateUserRequest;
import org.example.waspapi.dto.responses.users.GetUserResponse;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.User;
import org.example.waspapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Users", description = "Endpoints for managing users")
@RequestMapping("/users")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Endpoint for user login.
   *
   * <p>Handles HTTP GET requests to the `/login` endpoint. Uses the authenticated user's JWT token
   * to extract the email and retrieve or create the corresponding user information. Logs
   * information about the login attempt and its result. Returns a 500 Internal Server Error
   * response in case of failure.
   *
   * @param jwt The authenticated user's JWT token, used to extract the email.
   * @return A ResponseEntity containing the User object: - 200 OK if the user is found or created
   *     successfully. - 400 Bad Request if a handled exception occurs. - 500 Internal Server Error
   *     if an error occurs during the process.
   */
  @Operation(
      summary = "User login",
      description =
          "Endpoint for user login using JWT authentication. Retrieves or creates user information based on the JWT token.",
      operationId = "userLogin")
  @GetMapping("/login")
  public ResponseEntity<User> login(@AuthenticationPrincipal Jwt jwt) {
    try {
      logger.info("User login attempt with JWT: {}", jwt.getTokenValue());
      String email = jwt.getClaim(AUTH0_AUDIENCE_EMAIL).toString();
      User user = userService.getOrCreate(email);

      logger.info("User logged in successfully: {}", user.getEmail());
      return ResponseEntity.ok(user);
    } catch (HandledException e) {
      logger.error("Handled exception while user login: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(null);
    } catch (Exception e) {
      logger.error("Error during user login: {}", e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Endpoint for updating user information.
   *
   * <p>Handles HTTP PUT requests to the `/update` endpoint. Uses the authenticated user's JWT token
   * to extract the email and update the user's information with the data provided in the request.
   * Logs information about the update attempt and its result. Returns a 404 Not Found response if
   * the user cannot be updated.
   *
   * @param jwt The authenticated user's JWT token, used to extract the email.
   * @param request An UpdateUserRequest object containing the updated user data.
   * @return A ResponseEntity containing the User object: - 200 OK if the user is updated
   *     successfully. - 400 Bad Request if a handled exception occurs. - 404 Not Found if an error
   *     occurs during the update process. - 500 Internal Server Error if an unexpected error
   *     occurs.
   */
  @Operation(
      summary = "Update user information",
      description =
          "Endpoint for updating user information using JWT authentication. Updates the user's data based on the provided request.",
      operationId = "updateUser")
  @PutMapping("/update")
  public ResponseEntity<User> updateUser(
      @AuthenticationPrincipal Jwt jwt, @RequestBody UpdateUserRequest request) {
    try {
      String email = jwt.getClaim(AUTH0_AUDIENCE_EMAIL).toString();
      logger.info("Updating user with email: {}, request {}", email, request);
      User updated = userService.update(email, request);

      logger.info("User updated successfully: {}", updated.getEmail());
      return ResponseEntity.ok(updated);
    } catch (HandledException e) {
      logger.error("Handled exception while updating user: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(null);
    } catch (Exception e) {
      logger.error("Error during updating user: {}", e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Endpoint para obtener la información del usuario autenticado.
   *
   * <p>Maneja solicitudes HTTP GET al endpoint `/me`. Utiliza el token JWT del usuario autenticado
   * para extraer su correo electrónico y recuperar la información del usuario correspondiente.
   * Registra información sobre el intento de recuperación y el resultado. Devuelve una respuesta
   * 404 Not Found si el usuario no es encontrado. Devuelve una respuesta 500 Internal Server Error
   * en caso de error.
   *
   * @param jwt El token JWT del usuario autenticado, utilizado para extraer el correo electrónico.
   * @return Un ResponseEntity que contiene el objeto User: - 200 OK si el usuario es encontrado
   *     exitosamente. - 400 Bad Request si ocurre una excepción manejada. - 404 Not Found si el
   *     usuario no es encontrado. - 500 Internal Server Error si ocurre un error durante el
   *     proceso.
   */
  @Operation(
      summary = "Get authenticated user information",
      description =
          "Endpoint to retrieve the authenticated user's information using JWT authentication.",
      operationId = "getAuthenticatedUser")
  @GetMapping("/me")
  public ResponseEntity<User> getMe(@AuthenticationPrincipal Jwt jwt) {
    try {
      String auth0Email = jwt.getClaim(AUTH0_AUDIENCE_EMAIL).toString();
      logger.info("Fetch me with email: {}", auth0Email);
      User user = userService.get(auth0Email);
      if (user == null) {
        logger.error("Me not found for email: {}", auth0Email);
        return ResponseEntity.notFound().build();
      }

      logger.info("Me fetched successfully: {}", user.getEmail());
      return ResponseEntity.ok(user);
    } catch (HandledException e) {
      logger.error("Handled exception while fetching me: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(null);
    } catch (Exception e) {
      logger.error("Error fetching user me: {}", e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Endpoint for retrieving information of a specific user.
   *
   * <p>Handles HTTP GET requests to the `/users/{email}` endpoint. Uses the authenticated user's
   * JWT token to authorize the request and retrieve the information of the user corresponding to
   * the provided email. Logs information about the retrieval attempt and its result. Returns a 404
   * Not Found response if the user is not found. Returns a 500 Internal Server Error response in
   * case of error.
   *
   * @param jwt The authenticated user's JWT token, used to authorize the request.
   * @param email The email of the user whose information is to be retrieved.
   * @return A ResponseEntity containing the GetUserResponse object: - 200 OK if the user is found
   *     successfully. - 400 Bad Request if a handled exception occurs. - 404 Not Found if the user
   *     is not found. - 500 Internal Server Error if an error occurs during the process.
   */
  @Operation(
      summary = "Get user by email",
      description =
          "Endpoint to retrieve a user's information by their email using JWT authentication.",
      operationId = "getUserByEmail")
  @GetMapping("/{email}")
  public ResponseEntity<GetUserResponse> getUser(
      @AuthenticationPrincipal Jwt jwt, @PathVariable String email) {
    try {
      logger.info("Fetching user with email: {}", email);
      User user = userService.get(email);
      if (user == null) {
        logger.error("User not found for email: {}", email);
        return ResponseEntity.notFound().build();
      }
      GetUserResponse response =
          new GetUserResponse(
              user.getNickname(),
              user.getBio(),
              user.getPreference(),
              user.getDisponibility(),
              user.getProfilePhoto());
      return ResponseEntity.ok(response);

    } catch (HandledException e) {
      logger.error("Handled exception while fetching user: {}", e.getMessage());
      return ResponseEntity.status(e.getStatusCode()).body(null);
    } catch (Exception e) {
      logger.error("Error fetching user: {}", e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }
}
