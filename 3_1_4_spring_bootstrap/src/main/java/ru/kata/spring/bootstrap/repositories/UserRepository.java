package ru.kata.spring.bootstrap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.bootstrap.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "truncate table users;", nativeQuery = true)
    void truncateUsers();

    @Modifying
    @Transactional
    @Query(value = "truncate table user_role;", nativeQuery = true)
    void truncateUser_role();

    @Modifying
    @Transactional
    @Query(value = "set foreign_key_checks = 0;", nativeQuery = true)
    void setForeignKeyChecksDisabled();

    @Modifying
    @Transactional
    @Query(value = "set foreign_key_checks = 1;", nativeQuery = true)
    void setForeignKeyChecksEnabled();
}
