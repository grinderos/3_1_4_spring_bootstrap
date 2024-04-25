package ru.kata.spring.bootstrap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.bootstrap.model.Role;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.repositories.RoleRepository;
import ru.kata.spring.bootstrap.repositories.UserRepository;

import java.util.HashSet;

@Service
public class UserService{
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public RoleRepository getRoleRepository() {
        return roleRepository;
    }

    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        user.setPassword(PasswordEncoder.bCryptPasswordEncoder().encode(user.getPassword()));
//        admin.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void fillUsers() {
        fillRoles();
        User admin = new User("admin", "admin_name", "admin_lastname",
                "admin@mail.com", 33, "admin");
        admin.setRoles(new HashSet<>(roleRepository.findAll()));
        User user = new User("admin", "user_name", "user_lastname",
                "admin@mail.com", 22, "admin");
        user.addRole(roleRepository.findByName("ROLE_USER"));
        User loadedUserFromDB = findByUsername(admin.getUsername());
        if (loadedUserFromDB == null) {
            save(admin);
        }
        loadedUserFromDB = null;
        loadedUserFromDB = findByUsername(user.getUsername());
        System.out.println("\n");
        System.out.println(loadedUserFromDB);
        System.out.println("\n");
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
}
