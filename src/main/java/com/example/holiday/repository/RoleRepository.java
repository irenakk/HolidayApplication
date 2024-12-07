package com.example.holiday.repository;

import com.example.holiday.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для управления сущностями {@link Role}.
 * Предоставляет методы для выполнения операций CRUD и поиска ролей по имени.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Ищет роль по её имени.
     *
     * @param roleName имя роли.
     * @return объект {@link Role}, если роль с указанным именем существует, или null в противном случае.
     */
    Role findByName(String roleName);
}
