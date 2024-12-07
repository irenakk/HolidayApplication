package com.example.holiday.repository;

import com.example.holiday.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для управления сущностями {@link Event}.
 * Предоставляет методы для выполнения операций CRUD и поиска мероприятий по различным критериям.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Ищет мероприятия, у которых название или статус содержат указанный текст (без учета регистра).
     *
     * @param title часть названия мероприятия.
     * @param status часть статуса мероприятия.
     * @return список мероприятий, соответствующих критериям поиска.
     */
    List<Event> findByTitleContainingIgnoreCaseOrStatusContainingIgnoreCase(String title, String status);

    /**
     * Ищет мероприятия, которые проводятся в указанную дату.
     *
     * @param date дата проведения мероприятия.
     * @return список мероприятий, соответствующих указанной дате.
     */
    List<Event> findByDate(LocalDateTime date);

    /**
     * Ищет мероприятия с указанным бюджетом.
     *
     * @param budget бюджет мероприятия.
     * @return список мероприятий с указанным бюджетом.
     */
    List<Event> findByBudget(Integer budget);

    /**
     * Ищет мероприятия по имени клиента.
     *
     * @param clientName имя клиента.
     * @return список мероприятий, связанных с клиентами, чьи имена соответствуют указанному тексту.
     */
    List<Event> findByClient_NameContainingIgnoreCase(String clientName);

    /**
     * Ищет мероприятия по названию площадки.
     *
     * @param venueName название площадки.
     * @return список мероприятий, связанных с площадками, чьи названия соответствуют указанному тексту.
     */
    List<Event> findByVenue_NameContainingIgnoreCase(String venueName);

    /**
     * Ищет мероприятия по имени менеджера.
     *
     * @param managerName имя менеджера.
     * @return список мероприятий, связанных с менеджерами, чьи имена соответствуют указанному тексту.
     */
    List<Event> findByManager_NameContainingIgnoreCase(String managerName);
}
