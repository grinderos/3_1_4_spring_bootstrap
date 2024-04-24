package ru.kata.spring.bootstrap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.spring.bootstrap.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
