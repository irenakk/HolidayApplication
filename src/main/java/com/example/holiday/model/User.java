package com.example.holiday.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс, представляющий сотрудника (далее пользователя) в системе.
 * Содержит информацию о пользователе, включая идентификатор, имя, логин, пароль,
 * телефон, электронную почту и роли. Реализована связь многие-ко-многим с ролями.
 */
@Data
@Entity
public class User {

    /**
     * Уникальный идентификатор пользователя.
     * Генерируется автоматически стратегией {@link GenerationType#IDENTITY}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя. Поле не может быть null.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Уникальный логин пользователя. Поле не может быть null.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Пароль пользователя. Поле не может быть null.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Уникальный номер телефона пользователя. Поле не может быть null.
     */
    @Column(nullable = false, unique = true)
    private String phone;

    /**
     * Уникальная электронная почта пользователя. Поле не может быть null.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Набор ролей, связанных с пользователем.
     * Реализована связь многие-ко-многим с использованием таблицы связей "user_roles".
     * Загрузка осуществляется с использованием режима {@link FetchType#EAGER}.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();


    /**
     * Конструктор по умолчанию.
     */
    public User() {}
}
