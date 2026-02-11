package org.example.waspapi.service;

import static org.example.waspapi.Constants.ATTENDANCE_NOT_FOUND;
import static org.example.waspapi.Constants.EVENT_NOT_FOUND;
import static org.example.waspapi.Constants.GAME_NOT_FOUND;
import static org.example.waspapi.Constants.USER_NOT_FOUND;

import java.util.List;
import java.util.UUID;
import org.example.waspapi.dto.requests.event.CreateEventRequest;
import org.example.waspapi.dto.requests.event.UpdateEventRequest;
import org.example.waspapi.exceptions.HandledException;
import org.example.waspapi.model.Event;
import org.example.waspapi.model.EventAttendance;
import org.example.waspapi.model.Game;
import org.example.waspapi.model.User;
import org.example.waspapi.repository.EventAttendanceRepository;
import org.example.waspapi.repository.EventRepository;
import org.example.waspapi.repository.GameRepository;
import org.example.waspapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class EventService {

  private static final Logger logger = LoggerFactory.getLogger(EventService.class);
  private final EventRepository eventRepository;
  private final EventAttendanceRepository attendanceRepository;
  private final GameRepository gameRepository;
  private final UserRepository userRepository;

  public EventService(
      EventRepository eventRepository,
      EventAttendanceRepository attendanceRepository,
      GameRepository gameRepository,
      UserRepository userRepository) {
    this.eventRepository = eventRepository;
    this.attendanceRepository = attendanceRepository;
    this.gameRepository = gameRepository;
    this.userRepository = userRepository;
  }

  public Event create(CreateEventRequest request, UUID gameId) {
    logger.debug("Creating event for game: {}", gameId);
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(() -> new HandledException(GAME_NOT_FOUND, HttpStatus.NOT_FOUND));

    Event event = new Event();
    event.setName(request.getName());
    event.setIsPresential(request.getIsPresential());
    event.setDatetime(request.getDatetime());
    event.setPlace(request.getPlace());
    event.setObservations(request.getObservations());
    event.setGame(game);

    Event saved = eventRepository.save(event);
    logger.info("Event created with ID: {}", saved.getId());
    return saved;
  }

  public Event update(UUID eventId, UpdateEventRequest request) {
    logger.debug("Updating event: {}", eventId);
    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> new HandledException(EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));

    event.setName(request.getName());
    event.setIsPresential(request.getIsPresential());
    event.setDatetime(request.getDatetime());
    event.setPlace(request.getPlace());
    event.setObservations(request.getObservations());

    Event saved = eventRepository.save(event);
    logger.info("Event updated: {}", eventId);
    return saved;
  }

  public void delete(UUID eventId) {
    logger.debug("Deleting event: {}", eventId);
    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> new HandledException(EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    eventRepository.delete(event);
    logger.info("Event deleted: {}", eventId);
  }

  public Event getById(UUID eventId) {
    logger.debug("Fetching event: {}", eventId);
    return eventRepository
        .findById(eventId)
        .orElseThrow(() -> new HandledException(EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));
  }

  public List<Event> getByGameId(UUID gameId) {
    logger.debug("Fetching events for game: {}", gameId);
    return eventRepository.findByGameId(gameId);
  }

  public EventAttendance confirmAttendance(UUID userId, UUID eventId) {
    logger.debug("Confirming attendance for user {} on event {}", userId, eventId);
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new HandledException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(() -> new HandledException(EVENT_NOT_FOUND, HttpStatus.NOT_FOUND));

    EventAttendance attendance =
        attendanceRepository
            .findByUserIdAndEventId(userId, eventId)
            .orElseGet(
                () -> {
                  EventAttendance a = new EventAttendance();
                  a.setUser(user);
                  a.setEvent(event);
                  return a;
                });
    attendance.setConfirmAssist(true);

    EventAttendance saved = attendanceRepository.save(attendance);
    logger.info("Attendance confirmed for user {} on event {}", userId, eventId);
    return saved;
  }

  public EventAttendance cancelAttendance(UUID userId, UUID eventId) {
    logger.debug("Cancelling attendance for user {} on event {}", userId, eventId);
    EventAttendance attendance =
        attendanceRepository
            .findByUserIdAndEventId(userId, eventId)
            .orElseThrow(() -> new HandledException(ATTENDANCE_NOT_FOUND, HttpStatus.NOT_FOUND));
    attendance.setConfirmAssist(false);

    EventAttendance saved = attendanceRepository.save(attendance);
    logger.info("Attendance cancelled for user {} on event {}", userId, eventId);
    return saved;
  }

  public List<EventAttendance> getAttendees(UUID eventId) {
    logger.debug("Fetching attendees for event: {}", eventId);
    return attendanceRepository.findByEventId(eventId);
  }

  public long countConfirmed(UUID eventId) {
    return attendanceRepository.findByEventId(eventId).stream()
        .filter(a -> Boolean.TRUE.equals(a.getConfirmAssist()))
        .count();
  }
}
