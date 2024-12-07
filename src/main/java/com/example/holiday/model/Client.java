package com.example.holiday.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Класс, представляющий клиента системы.
 * Содержит информацию о клиенте, включая его имя, контактные данные и название компании (если применимо).
 */
@Data
@Entity
public class Client {

    /**
     * Уникальный идентификатор клиента.
     * Генерируется автоматически стратегией {@link GenerationType#IDENTITY}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя клиента. Поле является необязательным.
     */
    private String name;

    /**
     * Уникальный адрес электронной почты клиента. Поле не может быть null.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Контактный телефон клиента. Поле не может быть null.
     */
    @Column(nullable = false)
    private String phone;

    /**
     * Название компании клиента. Поле является необязательным.
     */
    private String company;

    /**
     * Конструктор по умолчанию.
     */
    public Client() {}
}
