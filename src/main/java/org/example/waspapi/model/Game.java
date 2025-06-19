package org.example.waspapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "games", schema = "public")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "game_photo")
    private String gamePhoto;

    @Column(name = "max_players")
    private Short maxPlayers;

    @Column(name = "public")
    private Boolean isPublic;

    @ManyToOne
    @JoinColumn(name = "theme_id", foreignKey = @ForeignKey(name = "Games_theme_id_fkey"))
    private Theme theme;

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

    public String getGamePhoto() {
        return gamePhoto;
    }

    public void setGamePhoto(String gamePhoto) {
        this.gamePhoto = gamePhoto;
    }

    public Short getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Short maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
