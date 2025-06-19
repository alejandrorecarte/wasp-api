package org.example.waspapi.service;

import org.example.waspapi.model.User;
import org.example.waspapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User getOrCreateUser(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    String nickname = email.split("@")[0];
                    User newUser = new User(email, nickname);
                    return userRepository.save(newUser);
                });
    }

    public User updateUser(User updatedUser) {
        // 1. Opcional: comprobar si existe el usuario antes de actualizar
        Optional<User> existingUserOpt = userRepository.findById(updatedUser.getEmail());
        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User existingUser = existingUserOpt.get();

        existingUser.setNickname(updatedUser.getNickname());
        existingUser.setBio(updatedUser.getBio());
        existingUser.setPreference(updatedUser.getPreference());
        existingUser.setDisponibility(updatedUser.getDisponibility());
        existingUser.setProfilePhoto(updatedUser.getProfilePhoto());

        return userRepository.save(existingUser);
    }
}