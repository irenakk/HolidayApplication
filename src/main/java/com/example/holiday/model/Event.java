package com.example.holiday.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Класс, представляющий мероприятие в системе.
 * Содержит информацию о мероприятии, включая его название, дату, бюджет, статус, а также связанные сущности: клиента, площадку и менеджера.
 */
@Data
@Entity
public class Event {

    /**
     * Уникальный идентификатор мероприятия.
     * Генерируется автоматически стратегией {@link GenerationType#IDENTITY}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название мероприятия. Поле является необязательным.
     */
    private String title;

    /**
     * Дата и время проведения мероприятия.
     * Форматируется в соответствии с ISO 8601.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;

    /**
     * Бюджет мероприятия. Поле является необязательным.
     */
    private int budget;

    /**
     * Статус мероприятия (например, "планируется", "проведено", "отменено").
     * Поле является необязательным.
     */
    private String status;

    /**
     * Клиент, связанный с мероприятием.
     * Ссылка на сущность {@link Client}.
     */
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    /**
     * Площадка, где будет проходить мероприятие.
     * Ссылка на сущность {@link Venue}.
     */
    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    /**
     * Менеджер, ответственный за организацию мероприятия.
     * Ссылка на сущность {@link User}.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User manager;

    /**
     * Конструктор по умолчанию.
     */
    public Event() {}
}
