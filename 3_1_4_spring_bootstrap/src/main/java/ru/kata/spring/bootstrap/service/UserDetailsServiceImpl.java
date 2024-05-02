package ru.kata.spring.bootstrap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.bootstrap.model.Role;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.repositories.RoleRepository;
import ru.kata.spring.bootstrap.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return new org.springframework.security.core.userdetails.User
                (user.getUsername(), user.getPassword(), grantedAuthorities);
    }

    //<<<<< repository block >>>>>
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
        try {
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println("\nСохранение не удалось. Возможно имя пользователя уже существует в базе\n");
            return false;
        }
        return true;
    }

    @Transactional
    public boolean update(User user) {
        User loadedUserFromDB;
        if ((loadedUserFromDB = findByUsername(user.getUsername())) != null &&
                !loadedUserFromDB.getId().equals(user.getId())) {
            return false;
        }
        loadedUserFromDB = findUserById(user.getId());
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
        } catch (Exception e) {
            System.out.println("\nСохранение не удалось. Возможно имя пользователя уже существует в базе\n");
            return false;
        }
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
    //<<<<< ----- >>>>>

}