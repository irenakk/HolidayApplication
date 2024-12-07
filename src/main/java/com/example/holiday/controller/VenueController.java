package com.example.holiday.controller;

import com.example.holiday.model.Venue;
import com.example.holiday.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Контроллер REST API для управления площадками ({@link Venue}).
 * Предоставляет CRUD-операции, а также возможности фильтрации и сортировки.
 */
@RestController
@RequestMapping("/api/venues")
public class VenueController {

    /** Сервис для управления площадками. */
    @Autowired
    private VenueService venueService;

    /**
     * Возвращает список площадок с фильтрацией и сортировкой.
     * Доступ предоставляется администраторам, менеджерам и наблюдателям.
     *
     * @param keyword ключевое слово для поиска (необязательно).
     * @param sort порядок сортировки (необязательно):
     *             <ul>
     *                 <li>"a_to_z" - по имени (от А до Я)</li>
     *                 <li>"z_to_a" - по имени (от Я до А)</li>
     *                 <li>"few_to_many" - по вместимости (от меньшего к большему)</li>
     *                 <li>"many_to_few" - по вместимости (от большего к меньшему)</li>
     *                 <li>"cheap_to_expensive" - по цене аренды (от дешевых к дорогим)</li>
     *                 <li>"expensive_to_cheap" - по цене аренды (от дорогих к дешевым)</li>
     *             </ul>
     * @return список площадок с кешированием на 60 секунд.
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIEWER') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<Venue>> getVenues(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false) String sort) {
        List<Venue> venues = venueService.listAll(keyword);
        if ("a_to_z".equals(sort)) {
            venues.sort((v1, v2) -> v1.getName().compareToIgnoreCase(v2.getName()));
        } else if ("z_to_a".equals(sort)) {
            venues.sort((v1, v2) -> v2.getName().compareToIgnoreCase(v1.getName()));
        } else if ("few_to_many".equals(sort)) {
            venues.sort((v1, v2) -> Integer.compare(v1.getCapacity(), v2.getCapacity()));
        } else if ("many_to_few".equals(sort)) {
            venues.sort((v1, v2) -> Integer.compare(v2.getCapacity(), v1.getCapacity()));
        } else if ("cheap_to_expensive".equals(sort)) {
            venues.sort((v1, v2) -> Double.compare(v1.getPrice(), v2.getPrice()));
        } else if ("expensive_to_cheap".equals(sort)) {
            venues.sort((v1, v2) -> Double.compare(v2.getPrice(), v1.getPrice()));
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(venues);
    }

    /**
     * Возвращает информацию о площадке по её идентификатору.
     * Доступ предоставляется администраторам, менеджерам и наблюдателям.
     *
     * @param id идентификатор площадки.
     * @return объект площадки или статус 404 (Not Found).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIEWER') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Venue> getVenue(@PathVariable Long id) {
        Venue venue = venueService.get(id);
        return venue != null
                ? ResponseEntity.ok(venue)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Создает новую площадку.
     * Доступ предоставляется администраторам и менеджерам.
     *
     * @param venue данные новой площадки.
     * @return созданная площадка со статусом 201 (Created).
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        Venue savedVenue = venueService.save(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVenue);
    }

    /**
     * Обновляет информацию о существующей площадке.
     * Доступ предоставляется администраторам и менеджерам.
     *
     * @param id идентификатор обновляемой площадки.
     * @param venue новые данные площадки.
     * @return обновленная площадка или статус ошибки.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody Venue venue) {
        Venue existingVenue = venueService.get(id);
        if (existingVenue == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        venue.setId(id);
        Venue updatedVenue = venueService.save(venue);
        return ResponseEntity.ok(updatedVenue);
    }

    /**
     * Удаляет площадку по её идентификатору.
     * Доступ предоставляется только администраторам.
     *
     * @param id идентификатор площадки.
     * @return статус 204 (No Content).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
