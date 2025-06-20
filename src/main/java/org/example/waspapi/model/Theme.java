package org.example.waspapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "themes", schema = "public")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "theme_photo")
    private String themePhoto;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
