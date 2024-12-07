package com.example.holiday.controller;

import com.example.holiday.model.Client;
import com.example.holiday.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Контроллер REST API для управления клиентами ({@link Client}).
 * Предоставляет CRUD-операции и возможность поиска и сортировки.
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    /** Сервис для управления клиентами. */
    @Autowired
    private ClientService clientService;

    /**
     * Возвращает список клиентов с возможностью фильтрации и сортировки.
     * Доступ предоставляется администраторам, менеджерам и наблюдателям.
     *
     * @param keyword ключевое слово для поиска (необязательно).
     * @param sort порядок сортировки (необязательно):
     *             <ul>
     *                 <li>"a_to_z" - по имени (от А до Я)</li>
     *                 <li>"z_to_a" - по имени (от Я до А)</li>
     *             </ul>
     * @return список клиентов с кешированием на 60 секунд.
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIEWER') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<Client>> getClients(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false) String sort) {
        List<Client> clients = clientService.listAll(keyword);
        if ("a_to_z".equals(sort)) {
            clients.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        } else if ("z_to_a".equals(sort)) {
            clients.sort((c1, c2) -> c2.getName().compareToIgnoreCase(c1.getName()));
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(clients);
    }

    /**
     * Возвращает информацию о клиенте по его идентификатору.
     * Доступ предоставляется администраторам, менеджерам и наблюдателям.
     *
     * @param id идентификатор клиента.
     * @return объект клиента или статус 404 (Not Found).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIEWER') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        Client client = clientService.get(id);
        return client != null
                ? ResponseEntity.ok(client)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Создает нового клиента.
     * Доступ предоставляется администраторам и менеджерам.
     *
     * @param client данные нового клиента.
     * @return созданный клиент со статусом 201 (Created).
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client savedClient = clientService.save(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    /**
     * Обновляет информацию о существующем клиенте.
     * Доступ предоставляется администраторам и менеджерам.
     *
     * @param id идентификатор обновляемого клиента.
     * @param client новые данные клиента.
     * @return обновленный клиент или статус ошибки.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        Client existingClient = clientService.get(id);
        if (existingClient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        client.setId(id);
        Client updatedClient = clientService.save(client);
        return ResponseEntity.ok(updatedClient);
    }

    /**
     * Удаляет клиента по его идентификатору.
     * Доступ предоставляется только администраторам.
     *
     * @param id идентификатор клиента.
     * @return статус 204 (No Content).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
