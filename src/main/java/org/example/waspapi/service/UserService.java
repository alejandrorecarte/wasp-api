package org.example.waspapi.service;

import static org.example.waspapi.Constants.USER_NOT_FOUND;

import java.util.Optional;
import org.example.waspapi.dto.requests.users.UpdateUserRequest;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Retrieves an existing user by their email or creates a new one if not found.
   *
   * <p>This method searches the user repository for a user with the specified email. If no user is
   * found, it generates a new user with a nickname based on the email and a random number, then
   * saves the new user to the repository.
   *
   * @param email The email of the user to retrieve or create.
   * @return The User object, either retrieved from the repository or newly created.
   */
  public User getOrCreate(String email) {
    return userRepository
        .findByEmail(email)
        .orElseGet(
            () -> {
              String nickname = email.split("@")[0] + Math.random() * 1000;
              User newUser = new User(email, nickname);
              return userRepository.save(newUser);
            });
  }

  /**
   * Retrieves a user by their email.
   *
   * <p>This method searches the user repository for a user with the specified email. If no user is
   * found, it returns null.
   *
   * @param email The email of the user to retrieve.
   * @return The User object if found, or null if no user exists with the given email.
   */
  public User get(String email) {
    return userRepository.findByEmail(email).orElseGet(() -> null);
  }

  /**
   * Updates an existing user's information in the system.
   *
   * <p>This method retrieves a user by their email and updates their details based on the provided
   * request. If the user does not exist, it throws a HandledException with a NOT_FOUND status. The
   * updated user is then saved back to the repository.
   *
   * @param email The email of the user to be updated.
   * @param request An UpdateUserRequest object containing the new user details.
   * @return The updated User object saved in the repository.
   * @throws HandledException If the user is not found in the repository.
   */
  public User update(String email, UpdateUserRequest request) {
    Optional<User> existingUserOpt = userRepository.findById(email);
    if (existingUserOpt.isEmpty()) {
      throw new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    User existingUser = existingUserOpt.get();

    existingUser.setNickname(request.getNickname());
    existingUser.setBio(request.getBio());
    existingUser.setPreference(request.getPreference());
    existingUser.setDisponibility(request.getDisponibility());
    existingUser.setProfilePhoto(request.getProfilePhoto());

    return userRepository.save(existingUser);
  }
}
