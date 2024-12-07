package com.example.holiday.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Класс, представляющий площадку для проведения мероприятий.
 * Содержит информацию о месте, включая его название, контактный телефон, адрес, вместимость и стоимость аренды.
 */
@Data
@Entity
public class Venue {

    /**
     * Уникальный идентификатор площадки.
     * Генерируется автоматически стратегией {@link GenerationType#IDENTITY}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название площадки. Поле является необязательным.
     */
    private String name;

    /**
     * Контактный телефон площадки. Поле не может быть null.
     */
    @Column(nullable = false)
    private String phone;

    /**
     * Адрес площадки. Поле является необязательным.
     */
    private String address;

    /**
     * Вместимость площадки (максимальное количество человек).
     * Поле является необязательным.
     */
    private int capacity;

    /**
     * Стоимость аренды площадки.
     * Поле является необязательным.
     */
    private int price;

    /**
     * Конструктор по умолчанию.
     */
    public Venue() {}
}
