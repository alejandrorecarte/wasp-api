package org.example.waspapi.repository;

import java.util.Optional;
import java.util.UUID;
import org.example.waspapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);

  Optional<User> findByNickname(String nickname);

  boolean existsByNickname(String nickname);
}
