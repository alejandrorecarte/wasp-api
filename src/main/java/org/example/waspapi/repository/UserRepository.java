package org.example.waspapi.repository;

import java.util.Optional;
import org.example.waspapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByEmail(String email);

  boolean existsByNickname(String nickname);
}
