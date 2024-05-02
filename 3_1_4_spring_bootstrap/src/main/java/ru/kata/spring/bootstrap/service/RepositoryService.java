package ru.kata.spring.bootstrap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.bootstrap.model.Role;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.repositories.RoleRepository;
import ru.kata.spring.bootstrap.repositories.UserRepository;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class RepositoryService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public RepositoryService(UserRepository userRepository, RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        Optional<User> userFromDb = userRepository.findById(id);
        return userFromDb.orElse(new User());
    }

    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Transactional
    public boolean save(User user) {
        User loadedUserFromDB = findByUsername(user.getUsername());
        if (loadedUserFromDB != null) {
            return false;
        }
        user.setPassword(PasswordEncoder.bCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean update(User user) {
        System.out.println("\nПОПЫТКА ОБНОВИТЬ ЮЗЕРА:\n"+user);
        User loadedUserFromDB;
        if ((loadedUserFromDB = findUserById(user.getId())) == null) {
            return false;
        }
        if (user.getPassword() == null ||
                user.getPassword().equals(loadedUserFromDB.getPassword()) ||
                user.getPassword().length() == 0) {
            user.setPassword(loadedUserFromDB.getPassword());
        } else {
            user.setPassword(PasswordEncoder.bCryptPasswordEncoder()
                    .encode(user.getPassword()));
        }
        try {
            userRepository.save(user);
        } catch (Exception e) {}
        System.out.println("\nЮЗЕР ОБНОВЛЕН");
        return true;
    }

    @Transactional
    public void deleteUserById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        }
    }

    @Transactional
    public void fillUsers() {
        fillRoles();
        User admin = new User("admin", "admin_name", "admin_lastname",
                "admin@mail.com", 33, "admin");
        admin.setRoles(new HashSet<>(roleRepository.findAll()));
        User user = new User("user", "user_name", "user_lastname",
                "user@mail.com", 22, "user");
        user.addRole(roleRepository.findByName("ROLE_USER"));
        User loadedUserFromDB = findByUsername(admin.getUsername());
        if (loadedUserFromDB == null) {
            save(admin);
        }
        loadedUserFromDB = null;
        loadedUserFromDB = findByUsername(user.getUsername());
        if (loadedUserFromDB == null) {
            save(user);
        }
    }

    @Transactional
    public void fillRoles() {
        if (roleRepository.findAll().isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_USER"));
        }
    }

    @Transactional
    public void truncate() {
        userRepository.setForeignKeyChecksDisabled();
        userRepository.truncateUsers();
        userRepository.truncateUser_role();
        userRepository.setForeignKeyChecksEnabled();
    }
}
