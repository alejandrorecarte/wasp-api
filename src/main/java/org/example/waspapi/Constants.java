package org.example.waspapi;

public class Constants {
  public static final String SUPABASE_EMAIL_CLAIM = "email";

  // ERRORS
  public static final String USER_NOT_FOUND = "User not found";
  public static final String GAME_NOT_FOUND = "Game not found";
  public static final String THEME_NOT_FOUND = "Theme not found";
  public static final String SUBSCRIPTION_NOT_FOUND = "Subscription not found";
  public static final String USER_ALREADY_EXISTS = "User already exists";
  public static final String NICKNAME_ALREADY_EXISTS = "Nickname already taken";
  public static final String JOIN_REQUEST_NOT_FOUND = "Join request not found";
  public static final String ALREADY_SUBSCRIBED = "User is already subscribed to this game";
  public static final String JOIN_REQUEST_ALREADY_EXISTS =
      "A join request already exists for this game";

  public static final String GAME_FULL = "The game is already full";
  public static final String CANNOT_LEAVE_AS_OWNER = "The game owner cannot leave the game";
  public static final String NOT_SUBSCRIBED = "User is not subscribed to this game";

  public static final String FRIEND_REQUEST_NOT_FOUND = "Friend request not found";
  public static final String FRIEND_REQUEST_ALREADY_EXISTS =
      "A friend request already exists between these users";
  public static final String ALREADY_FRIENDS = "Users are already friends";
  public static final String CANNOT_FRIEND_SELF = "Cannot send a friend request to yourself";
  public static final String FRIEND_NOT_FOUND = "Friendship not found";

  public static final String PHOTO_UPLOAD_FAILED = "Failed to upload photo";
  public static final String INVALID_FILE_TYPE = "Invalid file type. Only images are allowed";

  public static final String SESSION_NOT_FOUND = "Session not found";
  public static final String ATTENDANCE_NOT_FOUND = "Attendance not found";

  public static final String MESSAGE_CONTENT_EMPTY = "Message content cannot be empty";

  public static final String NOT_FRIENDS = "Users are not friends";
  public static final String PRIVATE_MESSAGE_CONTENT_EMPTY =
      "Private message content cannot be empty";

  public static final String NOTIFICATION_NOT_FOUND = "Notification not found";

  public static final String CHARACTER_SHEET_NOT_FOUND = "Character sheet not found";
}
