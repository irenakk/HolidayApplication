package com.example.holiday.controller;

import com.example.holiday.model.Client;
import com.example.holiday.model.Event;
import com.example.holiday.model.User;
import com.example.holiday.model.Venue;
import com.example.holiday.service.ClientService;
import com.example.holiday.service.EventService;
import com.example.holiday.service.UserService;
import com.example.holiday.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Контроллер REST API для управления мероприятиями ({@link Event}).
 * Предоставляет CRUD-операции для работы с мероприятиями, а также возможности фильтрации, сортировки
 * и получения связанных данных (клиенты, площадки, менеджеры).
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    /** Сервис для управления мероприятиями. */
    @Autowired
    private EventService eventService;

    /** Сервис для управления пользователями. */
    @Autowired
    private UserService userService;

    /** Сервис для управления клиентами. */
    @Autowired
    private ClientService clientService;

    /** Сервис для управления площадками. */
    @Autowired
    private VenueService venueService;

    /**
     * Возвращает список мероприятий с фильтрацией и сортировкой.
     * Доступ предоставляется администраторам, менеджерам и наблюдателям.
     *
     * @param keyword ключевое слово для поиска (необязательно).
     * @param sort порядок сортировки (необязательно):
     *             <ul>
     *                 <li>"title_a_to_z" - по названию (от А до Я)</li>
     *                 <li>"title_z_to_a" - по названию (от Я до А)</li>
     *                 <li>"new_to_old" - по дате (от новых к старым)</li>
     *                 <li>"old_to_new" - по дате (от старых к новым)</li>
     *                 <li>"cheap_to_expensive" - по бюджету (от дешевых к дорогим)</li>
     *                 <li>"expensive_to_cheap" - по бюджету (от дорогих к дешевым)</li>
     *                 <li>"status_a_to_z" - по статусу (от А до Я)</li>
     *                 <li>"status_z_to_a" - по статусу (от Я до А)</li>
     *             </ul>
     * @return список мероприятий с кешированием на 60 секунд.
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIEWER') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<Event>> getEvents(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false) String sort) {
        List<Event> events = eventService.listAll(keyword);
        if ("title_a_to_z".equals(sort)) {
            events.sort(Comparator.comparing(Event::getTitle));
        } else if ("title_z_to_a".equals(sort)) {
            events.sort(Comparator.comparing(Event::getTitle).reversed());
        } else if ("new_to_old".equals(sort)) {
            events.sort(Comparator.comparing(Event::getDate));
        } else if ("old_to_new".equals(sort)) {
            events.sort(Comparator.comparing(Event::getDate).reversed());
        } else if ("cheap_to_expensive".equals(sort)) {
            events.sort(Comparator.comparing(Event::getBudget));
        } else if ("expensive_to_cheap".equals(sort)) {
            events.sort(Comparator.comparing(Event::getBudget).reversed());
        } else if ("status_a_to_z".equals(sort)) {
            events.sort(Comparator.comparing(Event::getStatus));
        } else if ("status_z_to_a".equals(sort)) {
            events.sort(Comparator.comparing(Event::getStatus).reversed());
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(events);
    }

    /**
     * Возвращает информацию о мероприятии по его идентификатору.
     * Доступ предоставляется администраторам, менеджерам и наблюдателям.
     *
     * @param id идентификатор мероприятия.
     * @return объект мероприятия или статус 404 (Not Found).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIEWER') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        Event event = eventService.get(id);
        return event != null
                ? ResponseEntity.ok(event)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Создает новое мероприятие.
     * Доступ предоставляется администраторам и менеджерам.
     *
     * @param event данные нового мероприятия.
     * @return созданное мероприятие со статусом 201 (Created).
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event savedEvent = eventService.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    /**
     * Обновляет информацию о существующем мероприятии.
     * Доступ предоставляется администраторам и менеджерам.
     *
     * @param id идентификатор обновляемого мероприятия.
     * @param event новые данные мероприятия.
     * @return обновленное мероприятие или статус ошибки.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Event existingEvent = eventService.get(id);
        if (existingEvent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        existingEvent.setTitle(event.getTitle());
        existingEvent.setDate(event.getDate());
        existingEvent.setBudget(event.getBudget());
        existingEvent.setStatus(event.getStatus());
        existingEvent.setClient(event.getClient());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setManager(event.getManager());

        Event updatedEvent = eventService.save(existingEvent);
        return ResponseEntity.ok(updatedEvent);
    }

    /**
     * Удаляет мероприятие по его идентификатору.
     * Доступ предоставляется только администраторам.
     *
     * @param id идентификатор мероприятия.
     * @return статус 204 (No Content).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает список всех клиентов.
     * Доступ предоставляется администраторам и менеджерам.
     *
     * @return список клиентов.
     */
    @GetMapping("/clients")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIEWER') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<Client>> getClients() {
        List<Client> clients = clientService.listAll(null);
        return ResponseEntity.ok(clients);
    }

    /**
     * Возвращает список всех площадок.
     * Доступ предоставляется администраторам и менеджерам.
     *
     * @return список площадок.
     */
    @GetMapping("/venues")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIEWER') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<Venue>> getVenues() {
        List<Venue> venues = venueService.listAll(null);
        return ResponseEntity.ok(venues);
    }

    /**
     * Возвращает список всех менеджеров.
     * Доступ предоставляется администраторам и менеджерам.
     *
     * @return список менеджеров.
     */
    @GetMapping("/managers")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIEWER') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<User>> getManagers() {
        List<User> managers = userService.listAll(null);
        return ResponseEntity.ok(managers);
    }

    /**
     * Возвращает список всех мероприятий для диаграмм.
     * Доступ предоставляется всем пользователям.
     *
     * @return список мероприятий для отображения на графике.
     */
    @GetMapping("/chart")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIEWER') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<Event>> getEventsForChart() {
        List<Event> listEvents = eventService.listAll(null);
        return ResponseEntity.ok(listEvents);
    }
}
