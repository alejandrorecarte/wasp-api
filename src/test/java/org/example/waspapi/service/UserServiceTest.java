package org.example.waspapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.example.waspapi.dto.requests.users.RegisterUserRequest;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  private User existingUser;
  private RegisterUserRequest registerRequest;
  private UUID existingUserId;
  private UUID newUserId;

  @BeforeEach
  void setUp() {
    existingUserId = UUID.randomUUID();
    newUserId = UUID.randomUUID();

    existingUser = new User(existingUserId, "test@email.com", "TestUser");
    existingUser.setBio("A bio");
    existingUser.setPreference("pref");
    existingUser.setDisponibility("weekends");
    existingUser.setProfilePhoto("photo.jpg");

    registerRequest = new RegisterUserRequest("NewNick", "bio", "pref", "weekends", "photo.jpg");
  }

  // --- getOrCreate (login) tests ---

  @Test
  void getOrCreate_existingUser_returnsUser() {
    when(userRepository.findById(existingUserId)).thenReturn(Optional.of(existingUser));

    User result = userService.getOrCreate(existingUserId, "test@email.com");

    assertEquals("test@email.com", result.getEmail());
    assertEquals("TestUser", result.getNickname());
    verify(userRepository, never()).save(any());
  }

  @Test
  void getOrCreate_newUser_createsAndReturnsUser() {
    when(userRepository.findById(newUserId)).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = userService.getOrCreate(newUserId, "new@email.com");

    assertEquals("new@email.com", result.getEmail());
    assertEquals(newUserId, result.getId());
    assertTrue(result.getNickname().startsWith("new"));
    verify(userRepository).save(any(User.class));
  }

  // --- register tests ---

  @Test
  void register_success_returnsNewUser() {
    when(userRepository.findByEmail("new@email.com")).thenReturn(Optional.empty());
    when(userRepository.existsByNickname("NewNick")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result = userService.register(newUserId, "new@email.com", registerRequest);

    assertEquals("new@email.com", result.getEmail());
    assertEquals(newUserId, result.getId());
    assertEquals("NewNick", result.getNickname());
    assertEquals("bio", result.getBio());
    assertEquals("pref", result.getPreference());
    assertEquals("weekends", result.getDisponibility());
    assertEquals("photo.jpg", result.getProfilePhoto());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void register_emailAlreadyExists_throwsConflict() {
    when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(existingUser));

    HandledException ex =
        assertThrows(
            HandledException.class,
            () -> userService.register(newUserId, "test@email.com", registerRequest));

    assertEquals("User already exists", ex.getMessage());
    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    verify(userRepository, never()).save(any());
  }

  @Test
  void register_nicknameAlreadyExists_throwsConflict() {
    when(userRepository.findByEmail("new@email.com")).thenReturn(Optional.empty());
    when(userRepository.existsByNickname("NewNick")).thenReturn(true);

    HandledException ex =
        assertThrows(
            HandledException.class,
            () -> userService.register(newUserId, "new@email.com", registerRequest));

    assertEquals("Nickname already taken", ex.getMessage());
    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    verify(userRepository, never()).save(any());
  }
}
