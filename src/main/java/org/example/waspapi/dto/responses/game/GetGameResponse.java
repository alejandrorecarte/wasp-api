package org.example.waspapi.dto.responses.game;

import java.util.List;
import java.util.UUID;

public class GetGameResponse {

  private UUID gameId;
  private String name;
  private String description;
  private String gamePhoto;
  private Short maxPlayers;
  private Boolean isPublic;
  private String themeName;
  private UUID masterUserId;
  private long playerCount;
  private List<PlayerInfo> players;

  public GetGameResponse() {}

  public GetGameResponse(
      UUID gameId,
      String name,
      String description,
      String gamePhoto,
      Short maxPlayers,
      Boolean isPublic,
      String themeName,
      UUID masterUserId,
      long playerCount,
      List<PlayerInfo> players) {
    this.gameId = gameId;
    this.name = name;
    this.description = description;
    this.gamePhoto = gamePhoto;
    this.maxPlayers = maxPlayers;
    this.isPublic = isPublic;
    this.themeName = themeName;
    this.masterUserId = masterUserId;
    this.playerCount = playerCount;
    this.players = players;
  }

  public UUID getGameId() {
    return gameId;
  }

  public void setGameId(UUID gameId) {
    this.gameId = gameId;
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

  public String getThemeName() {
    return themeName;
  }

  public void setThemeName(String themeName) {
    this.themeName = themeName;
  }

  public UUID getMasterUserId() {
    return masterUserId;
  }

  public void setMasterUserId(UUID masterUserId) {
    this.masterUserId = masterUserId;
  }

  public long getPlayerCount() {
    return playerCount;
  }

  public void setPlayerCount(long playerCount) {
    this.playerCount = playerCount;
  }

  public List<PlayerInfo> getPlayers() {
    return players;
  }

  public void setPlayers(List<PlayerInfo> players) {
    this.players = players;
  }

  public static class PlayerInfo {

    private UUID userId;
    private String nickname;
    private String role;
    private String profilePhoto;

    public PlayerInfo() {}

    public PlayerInfo(UUID userId, String nickname, String role, String profilePhoto) {
      this.userId = userId;
      this.nickname = nickname;
      this.role = role;
      this.profilePhoto = profilePhoto;
    }

    public UUID getUserId() {
      return userId;
    }

    public void setUserId(UUID userId) {
      this.userId = userId;
    }

    public String getNickname() {
      return nickname;
    }

    public void setNickname(String nickname) {
      this.nickname = nickname;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public String getProfilePhoto() {
      return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
      this.profilePhoto = profilePhoto;
    }
  }
}
