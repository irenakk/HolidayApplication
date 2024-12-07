package com.example.holiday.repository;

import com.example.holiday.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link Client}.
 * Предоставляет методы для выполнения операций CRUD и поиска клиентов по различным критериям.
 */
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Ищет клиентов, у которых имя, адрес электронной почты, телефон или название компании содержат указанный текст (без учета регистра).
     *
     * @param name часть имени клиента.
     * @param email часть адреса электронной почты клиента.
     * @param phone часть номера телефона клиента.
     * @param company часть названия компании клиента.
     * @return список клиентов, соответствующих критериям поиска.
     */
    List<Client> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrCompanyContainingIgnoreCase(
            String name, String email, String phone, String company);
}
