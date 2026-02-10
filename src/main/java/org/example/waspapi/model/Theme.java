package org.example.waspapi.model;

import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "themes", schema = "public")
public class Theme {

  @Id
  @GeneratedValue
  @Column(name = "theme_id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "text")
  private String description;

  @Column(name = "theme_photo")
  private String themePhoto;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getThemePhoto() {
    return themePhoto;
  }

  public void setThemePhoto(String themePhoto) {
    this.themePhoto = themePhoto;
  }
}
