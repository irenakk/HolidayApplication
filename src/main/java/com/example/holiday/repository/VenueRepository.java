package com.example.holiday.repository;

import com.example.holiday.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link Venue}.
 * Предоставляет методы для выполнения операций CRUD и дополнительные запросы для поиска площадок.
 */
public interface VenueRepository extends JpaRepository<Venue, Long> {

    /**
     * Ищет площадки, у которых имя, телефон или адрес содержат указанный текст (без учета регистра).
     *
     * @param name часть названия площадки.
     * @param phone часть номера телефона площадки.
     * @param address часть адреса площадки.
     * @return список площадок, соответствующих критериям поиска.
     */
    List<Venue> findByNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrAddressContainingIgnoreCase(
            String name, String phone, String address);

    /**
     * Ищет площадки с указанной вместимостью или стоимостью аренды.
     *
     * @param capacity вместимость площадки.
     * @param price стоимость аренды площадки.
     * @return список площадок, соответствующих критериям поиска.
     */
    List<Venue> findByCapacityOrPrice(Integer capacity, Integer price);
}
