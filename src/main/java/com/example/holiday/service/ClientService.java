package com.example.holiday.service;

import com.example.holiday.model.Client;
import com.example.holiday.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления сущностями {@link Client}.
 * Предоставляет методы для поиска, сохранения, получения и удаления клиентов.
 */
@Service
public class ClientService {

    /** Репозиторий для работы с клиентами. */
    @Autowired
    private ClientRepository clientRepository;

    /**
     * Возвращает список клиентов, соответствующих ключевому слову.
     * Ищет клиентов по имени, электронной почте, телефону или названию компании (без учета регистра).
     * Если ключевое слово отсутствует, возвращает всех клиентов.
     *
     * @param keyword ключевое слово для поиска.
     * @return список клиентов, соответствующих критериям поиска.
     */
    public List<Client> listAll(String keyword) {
        if (keyword != null) {
            return clientRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrCompanyContainingIgnoreCase(
                    keyword, keyword, keyword, keyword);
        }
        return clientRepository.findAll();
    }

    /**
     * Сохраняет клиента в базе данных.
     * Если клиент уже существует, его данные обновляются.
     *
     * @param client объект клиента для сохранения.
     * @return сохраненный клиент.
     */
    public Client save(Client client) {
        clientRepository.save(client);
        return client;
    }

    /**
     * Возвращает клиента по его идентификатору.
     * Если клиент отсутствует, выбрасывается исключение {@link java.util.NoSuchElementException}.
     *
     * @param id идентификатор клиента.
     * @return объект клиента.
     */
    public Client get(Long id) {
        return clientRepository.findById(id).get();
    }

    /**
     * Удаляет клиента по его идентификатору.
     *
     * @param id идентификатор клиента.
     */
    public void delete(Long id) {
        clientRepository.deleteById(id);
    }
}
