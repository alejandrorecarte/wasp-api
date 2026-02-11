package org.example.waspapi.model;

import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

  @Id
  @Column(name = "user_id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public String getNickname() {
    return nickname;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public void setPreference(String preference) {
    this.preference = preference;
  }

  public void setDisponibility(String disponibility) {
    this.disponibility = disponibility;
  }

  public void setProfilePhoto(String profilePhoto) {
    this.profilePhoto = profilePhoto;
  }

  public String getBio() {
    return bio;
  }

  public String getPreference() {
    return preference;
  }

  public String getDisponibility() {
    return disponibility;
  }

  public String getProfilePhoto() {
    return profilePhoto;
  }

  @Column(nullable = false)
  private String nickname;

  private String bio;

  private String preference;

  private String disponibility;

  @Column(name = "profile_photo")
  private String profilePhoto;

  // Constructores
  public User() {}

  public User(UUID id, String email, String nickname) {
    this.id = id;
    this.email = email;
    this.nickname = nickname;
  }
}
