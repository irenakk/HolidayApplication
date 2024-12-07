package com.example.holiday.service;

import com.example.holiday.model.Venue;
import com.example.holiday.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сервис для управления сущностями {@link Venue}.
 * Предоставляет методы для поиска, сохранения, получения и удаления площадок проведения мероприятий.
 */
@Service
public class VenueService {

    /** Репозиторий для работы с площадками. */
    @Autowired
    private VenueRepository venueRepository;

    /**
     * Возвращает список площадок, соответствующих ключевому слову.
     * Ищет площадки по названию, телефону, адресу, вместимости или цене (без учета регистра).
     * Если ключевое слово отсутствует, возвращает все площадки.
     *
     * @param keyword ключевое слово для поиска.
     * @return список площадок, соответствующих критериям поиска.
     */
    public List<Venue> listAll(String keyword) {
        if (keyword != null) {
            List<Venue> venuesByStringFields = venueRepository.findByNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrAddressContainingIgnoreCase(
                    keyword, keyword, keyword);

            List<Venue> venuesByCapacity = new ArrayList<>();
            try {
                Integer keywordInteger = Integer.parseInt(keyword);
                venuesByCapacity = venueRepository.findByCapacityOrPrice(keywordInteger, keywordInteger);
            } catch (NumberFormatException _) {}

            Set<Venue> combinedVenues = new HashSet<>(venuesByStringFields);
            combinedVenues.addAll(venuesByCapacity);

            return new ArrayList<>(combinedVenues);
        }
        return venueRepository.findAll();
    }

    /**
     * Сохраняет площадку в базе данных.
     * Если площадка уже существует, её данные обновляются.
     *
     * @param venue объект площадки для сохранения.
     * @return сохраненная площадка.
     */
    public Venue save(Venue venue) {
        venueRepository.save(venue);
        return venue;
    }

    /**
     * Возвращает площадку по её идентификатору.
     * Если площадка отсутствует, выбрасывается исключение {@link java.util.NoSuchElementException}.
     *
     * @param id идентификатор площадки.
     * @return объект площадки.
     */
    public Venue get(Long id) {
        return venueRepository.findById(id).get();
    }

    /**
     * Удаляет площадку по её идентификатору.
     *
     * @param id идентификатор площадки.
     */
    public void delete(Long id) {
        venueRepository.deleteById(id);
    }
}
