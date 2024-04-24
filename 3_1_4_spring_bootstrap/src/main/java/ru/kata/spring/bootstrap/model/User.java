package ru.kata.spring.bootstrap.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Поле не должно быть пустым")
    @Size(min = 3, max = 40, message = "Логин должен содержать не менее трех символов")
    @Column(name = "username", nullable = false, unique = true, length = 32)
    private String username;

    @Column(name = "first_name", nullable = false, unique = true, length = 16)
    private String first_name;

    @Column(name = "last_name", nullable = false, unique = true, length = 16)
    private String last_name;

    @Column(name = "email", nullable = false, unique = true, length = 32)
    private String email;

    @Column(name = "age")
    @Min(value = 0, message = "Неверно указан возраст")
    private Integer age;

    @NotEmpty(message = "Поле не должно быть пустым")
    @Column(name = "login_password", nullable = false, length = 64)
    @Size(min = 4, max = 100, message = "Пароль должен содержать не менее трех символов")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE) //@Fetch(FetchMode.JOIN)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Transient
    private String passwordConfirm;

    public User() {
    }

    public User(String username, String first_name, String last_name,
                String email, Integer age, String password) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.age = age;
        this.password = password;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}