package com.example.holiday.service;

import com.example.holiday.model.Event;
import com.example.holiday.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сервис для управления сущностями {@link Event}.
 * Предоставляет методы для поиска, сохранения, получения и удаления мероприятий.
 */
@Service
public class EventService {

    /** Репозиторий для работы с мероприятиями. */
    @Autowired
    private EventRepository eventRepository;

    /**
     * Возвращает список мероприятий, соответствующих ключевому слову.
     * Ищет мероприятия по названию, статусу, бюджету, дате проведения, имени клиента,
     * имени площадки или имени менеджера. Если ключевое слово отсутствует, возвращает все мероприятия.
     *
     * @param keyword ключевое слово для поиска.
     * @return список мероприятий, соответствующих критериям поиска.
     */
    public List<Event> listAll(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Event> eventsByStringFields = eventRepository.findByTitleContainingIgnoreCaseOrStatusContainingIgnoreCase(
                    keyword, keyword);

            List<Event> eventsByBudget = new ArrayList<>();
            try {
                Integer keywordInteger = Integer.parseInt(keyword);
                eventsByBudget = eventRepository.findByBudget(keywordInteger);
            } catch (NumberFormatException _) {}

            List<Event> eventsByDate = new ArrayList<>();
            try {
                LocalDateTime keywordDate = LocalDateTime.parse(keyword, DateTimeFormatter.ISO_DATE_TIME);
                eventsByDate = eventRepository.findByDate(keywordDate);
            } catch (DateTimeParseException _) {}

            List<Event> eventsByClientName = eventRepository.findByClient_NameContainingIgnoreCase(keyword);
            List<Event> eventsByVenueName = eventRepository.findByVenue_NameContainingIgnoreCase(keyword);
            List<Event> eventsByManagerName = eventRepository.findByManager_NameContainingIgnoreCase(keyword);

            Set<Event> combinedEvents = new HashSet<>(eventsByStringFields);
            combinedEvents.addAll(eventsByBudget);
            combinedEvents.addAll(eventsByDate);
            combinedEvents.addAll(eventsByClientName);
            combinedEvents.addAll(eventsByVenueName);
            combinedEvents.addAll(eventsByManagerName);

            return new ArrayList<>(combinedEvents);
        }
        return eventRepository.findAll();
    }

    /**
     * Сохраняет мероприятие в базе данных.
     * Если мероприятие уже существует, его данные обновляются.
     *
     * @param event объект мероприятия для сохранения.
     * @return сохраненное мероприятие.
     */
    public Event save(Event event) {
        eventRepository.save(event);
        return event;
    }

    /**
     * Возвращает мероприятие по его идентификатору.
     * Если мероприятие отсутствует, выбрасывается исключение {@link java.util.NoSuchElementException}.
     *
     * @param id идентификатор мероприятия.
     * @return объект мероприятия.
     */
    public Event get(Long id) {
        return eventRepository.findById(id).get();
    }

    /**
     * Удаляет мероприятие по его идентификатору.
     *
     * @param id идентификатор мероприятия.
     */
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }
}
