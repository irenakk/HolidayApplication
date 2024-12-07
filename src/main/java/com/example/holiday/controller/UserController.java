package com.example.holiday.controller;

import com.example.holiday.model.Role;
import com.example.holiday.model.User;
import com.example.holiday.service.RoleService;
import com.example.holiday.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Контроллер REST API для управления пользователями (сотрудниками) приложения ({@link User}).
 * Обрабатывает CRUD-операции, доступ к профилю текущего пользователя
 * и управление ролями.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /** Сервис для управления пользователями. */
    @Autowired
    private UserService userService;

    /** Сервис для управления ролями. */
    @Autowired
    private RoleService roleService;

    /**
     * Возвращает текущего авторизованного пользователя.
     *
     * @param principal текущий пользователь из контекста безопасности.
     * @return объект пользователя или статус 401 (Unauthorized).
     */
    @GetMapping("/currentUser")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Возвращает список пользователей с возможностью фильтрации и сортировки.
     *
     * @param keyword ключевое слово для поиска (необязательно).
     * @param sort порядок сортировки (необязательно):
     *             <ul>
     *                 <li>"a_to_z" - по имени (от А до Я)</li>
     *                 <li>"z_to_a" - по имени (от Я до А)</li>
     *             </ul>
     * @return список пользователей с кешированием на 60 секунд.
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false) String sort) {
        List<User> users = userService.listAll(keyword);
        if ("a_to_z".equals(sort)) {
            users.sort((u1, u2) -> u1.getName().compareToIgnoreCase(u2.getName()));
        } else if ("z_to_a".equals(sort)) {
            users.sort((u1, u2) -> u2.getName().compareToIgnoreCase(u1.getName()));
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(users);
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return объект пользователя или статус 404 (Not Found).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.get(id);
        return user != null
                ? ResponseEntity.ok(user)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Создает нового пользователя с заданными ролями.
     *
     * @param user данные нового пользователя.
     * @return созданный пользователь со статусом 201 (Created).
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        Set<Role> roles = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                Role existingRole = roleService.get(role.getId());
                if (existingRole != null) {
                    roles.add(existingRole);
                }
            });
        }
        user.setRoles(roles);
        User savedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    /**
     * Обновляет информацию о существующем пользователе.
     * Только администраторы или сам пользователь могут изменять данные.
     *
     * @param id идентификатор обновляемого пользователя.
     * @param user новые данные пользователя.
     * @param principal текущий авторизованный пользователь.
     * @return обновленный пользователь или статус ошибки.
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user, Principal principal) {
        User existingUser = userService.get(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User currentUser = userService.findByUsername(principal.getName());

        if (!currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))
                && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        existingUser.setName(user.getName());
        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());

        Set<Role> roles = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                Role existingRole = roleService.get(role.getId());
                if (existingRole != null) {
                    roles.add(existingRole);
                }
            });
        }
        existingUser.setRoles(roles);

        User updatedUser = userService.save(existingUser);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     * Доступно только для администраторов.
     *
     * @param id идентификатор пользователя.
     * @return статус 204 (No Content).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает список всех доступных ролей.
     * Доступно только для администраторов.
     *
     * @return список ролей.
     */
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = roleService.listAll();
        return ResponseEntity.ok(roles);
    }
}
