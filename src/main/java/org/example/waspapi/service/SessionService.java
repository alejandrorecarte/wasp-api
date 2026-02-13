package org.example.waspapi.service;

import static org.example.waspapi.Constants.ATTENDANCE_NOT_FOUND;
import static org.example.waspapi.Constants.GAME_NOT_FOUND;
import static org.example.waspapi.Constants.SESSION_NOT_FOUND;
import static org.example.waspapi.Constants.USER_NOT_FOUND;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.waspapi.dto.requests.session.CreateSessionRequest;
import org.example.waspapi.dto.requests.session.UpdateSessionRequest;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Game;
import org.example.waspapi.model.Session;
import org.example.waspapi.model.SessionAttendance;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.GameRepository;
import org.example.waspapi.repository.SessionAttendanceRepository;
import org.example.waspapi.repository.SessionRepository;
import org.example.waspapi.repository.SubscriptionRepository;
import org.example.waspapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

  private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
  private final SessionRepository sessionRepository;
  private final SessionAttendanceRepository attendanceRepository;
  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final SubscriptionService subscriptionService;
  private final SubscriptionRepository subscriptionRepository;
  private final NotificationService notificationService;

  public SessionService(
      SessionRepository sessionRepository,
      SessionAttendanceRepository attendanceRepository,
      GameRepository gameRepository,
      UserRepository userRepository,
      SubscriptionService subscriptionService,
      SubscriptionRepository subscriptionRepository,
      NotificationService notificationService) {
    this.sessionRepository = sessionRepository;
    this.attendanceRepository = attendanceRepository;
    this.gameRepository = gameRepository;
    this.userRepository = userRepository;
    this.subscriptionService = subscriptionService;
    this.subscriptionRepository = subscriptionRepository;
    this.notificationService = notificationService;
  }

  public Session create(CreateSessionRequest request, UUID gameId) {
    logger.debug("Creating session for game: {}", gameId);
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));

    Session session = new Session();
    session.setName(request.getName());
    session.setIsPresential(request.getIsPresential());
    session.setDatetime(request.getDatetime());
    session.setPlace(request.getPlace());
    session.setObservations(request.getObservations());
    session.setGame(game);

    Session saved = sessionRepository.save(session);
    logger.info("Session created with ID: {}", saved.getId());

    List<UUID> subscriberIds =
        subscriptionRepository.findByGameIdAndIsActiveTrue(gameId).stream()
            .map(s -> s.getUser().getId())
            .collect(Collectors.toList());
    notificationService.createForMany(subscriberIds, "SESSION_CREATED", saved.getId());
    return saved;
  }

  public Session update(UUID sessionId, UpdateSessionRequest request) {
    logger.debug("Updating session: {}", sessionId);
    Session session =
        sessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new HandledException(SESSION_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (request.getName() != null) {
      session.setName(request.getName());
    }
    if (request.getIsPresential() != null) {
      session.setIsPresential(request.getIsPresential());
    }
    if (request.getDatetime() != null) {
      session.setDatetime(request.getDatetime());
    }
    if (request.getPlace() != null) {
      session.setPlace(request.getPlace());
    }
    if (request.getObservations() != null) {
      session.setObservations(request.getObservations());
    }

    Session saved = sessionRepository.save(session);
    logger.info("Session updated: {}", sessionId);
    return saved;
  }

  public void delete(UUID sessionId) {
    logger.debug("Deleting session: {}", sessionId);
    Session session =
        sessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new HandledException(SESSION_NOT_FOUND, HttpStatus.NOT_FOUND));
    sessionRepository.delete(session);
    logger.info("Session deleted: {}", sessionId);
  }

  public Session getById(UUID sessionId) {
    logger.debug("Fetching session: {}", sessionId);
    return sessionRepository
        .findById(sessionId)
        .orElseThrow(() -> new HandledException(SESSION_NOT_FOUND, HttpStatus.NOT_FOUND));
  }

  public List<Session> getByGameId(UUID gameId) {
    logger.debug("Fetching sessions for game: {}", gameId);
    return sessionRepository.findByGameId(gameId);
  }

  public SessionAttendance confirmAttendance(UUID userId, UUID sessionId) {
    logger.debug("Confirming attendance for user {} on session {}", userId, sessionId);
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    Session session =
        sessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new HandledException(SESSION_NOT_FOUND, HttpStatus.NOT_FOUND));

    SessionAttendance attendance =
        attendanceRepository
            .findByUserIdAndSessionId(userId, sessionId)
            .orElseGet(
                () -> {
                  SessionAttendance a = new SessionAttendance();
                  a.setUser(user);
                  a.setSession(session);
                  return a;
                });
    attendance.setConfirmAssist(true);

    SessionAttendance saved = attendanceRepository.save(attendance);
    logger.info("Attendance confirmed for user {} on session {}", userId, sessionId);
    return saved;
  }

  public SessionAttendance cancelAttendance(UUID userId, UUID sessionId) {
    logger.debug("Cancelling attendance for user {} on session {}", userId, sessionId);
    SessionAttendance attendance =
        attendanceRepository
            .findByUserIdAndSessionId(userId, sessionId)
            .orElseThrow(() -> new HandledException(ATTENDANCE_NOT_FOUND, HttpStatus.NOT_FOUND));
    attendance.setConfirmAssist(false);

    SessionAttendance saved = attendanceRepository.save(attendance);
    logger.info("Attendance cancelled for user {} on session {}", userId, sessionId);
    return saved;
  }

  public SessionAttendance resetAttendance(UUID userId, UUID sessionId) {
    logger.debug("Resetting attendance to pending for user {} on session {}", userId, sessionId);
    SessionAttendance attendance =
        attendanceRepository
            .findByUserIdAndSessionId(userId, sessionId)
            .orElseThrow(() -> new HandledException(ATTENDANCE_NOT_FOUND, HttpStatus.NOT_FOUND));
    attendance.setConfirmAssist(null);

    SessionAttendance saved = attendanceRepository.save(attendance);
    logger.info("Attendance reset to pending for user {} on session {}", userId, sessionId);
    return saved;
  }

  public List<SessionAttendance> getAttendees(UUID sessionId) {
    logger.debug("Fetching attendees for session: {}", sessionId);
    return attendanceRepository.findBySessionId(sessionId);
  }

  public List<Session> getByUserAndMonth(UUID userId, int year, int month) {
    logger.debug("Fetching sessions for user {} in {}-{}", userId, year, month);
    List<UUID> gameIds =
        subscriptionService.getGamesByUserId(userId).stream()
            .map(Game::getId)
            .collect(Collectors.toList());
    if (gameIds.isEmpty()) {
      return Collections.emptyList();
    }
    YearMonth ym = YearMonth.of(year, month);
    LocalDateTime start = ym.atDay(1).atStartOfDay();
    LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();
    return sessionRepository.findByGameIdsAndDateRange(gameIds, start, end);
  }

  public long countConfirmed(UUID sessionId) {
    return attendanceRepository.findBySessionId(sessionId).stream()
        .filter(a -> Boolean.TRUE.equals(a.getConfirmAssist()))
        .count();
  }
}
