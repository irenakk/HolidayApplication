package com.example.holiday.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Класс, представляющий роль в системе.
 * Роль используется для определения прав доступа пользователя.
 */
@Data
@Entity
public class Role {

    /**
     * Уникальный идентификатор роли.
     * Генерируется автоматически стратегией {@link GenerationType#IDENTITY}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальное имя роли. Поле не может быть null.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Описание роли. Поле является необязательным.
     */
    private String description;

    /**
     * Конструктор по умолчанию.
     */
    public Role() {}
}
