package ru.kata.spring.bootstrap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.bootstrap.model.Role;
import ru.kata.spring.bootstrap.model.User;
import ru.kata.spring.bootstrap.repositories.RoleRepository;
import ru.kata.spring.bootstrap.repositories.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserService userService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new org.springframework.security.core.userdetails.User
                (user.getUsername(), user.getPassword(), grantedAuthorities);
    }


    @Transactional
    public void fillUsers() {
        fillRoles();
        User admin = new User("admin", "admin_name", "admin_lastname",
                "admin@mail.com", 33, "admin");
        admin.setRoles(new HashSet<>(userService.getRoleRepository().findAll()));
        User user = new User("user", "user_name", "user_lastname",
                "user@mail.com", 22, "user");
        user.addRole(userService.getRoleRepository().findByName("ROLE_USER"));
        User loadedUserFromDB = userService.findByUsername(admin.getUsername());
        if (loadedUserFromDB == null) {
            userService.save(admin);
        }
        loadedUserFromDB = null;
        loadedUserFromDB = userService.findByUsername(user.getUsername());
        System.out.println("\n");
        System.out.println(loadedUserFromDB);
        System.out.println("\n");
        if (loadedUserFromDB == null) {
            userService.save(user);
        }
    }

    @Transactional
    public void fillRoles() {
        if (userService.getRoleRepository().findAll().isEmpty()) {
            userService.getRoleRepository().save(new Role("ROLE_ADMIN"));
            userService.getRoleRepository().save(new Role("ROLE_USER"));
        }
    }

    public UserService getUserService() {
        return userService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}