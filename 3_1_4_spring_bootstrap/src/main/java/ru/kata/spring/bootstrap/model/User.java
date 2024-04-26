package ru.kata.spring.bootstrap.model;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
//@Data
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Поле не должно быть пустым")
//    @Size(min = 3, max = 40, message = "Логин должен содержать не менее трех символов")
    @Column(name = "username", nullable = false, unique = true, length = 32)
    private String username;

    @Column(name = "firstname", nullable = true, unique = false, length = 16)
    private String firstname;

    @Column(name = "lastname", nullable = true, unique = false, length = 16)
    private String lastname;

    @Column(name = "email", nullable = true, unique = true, length = 32)
    private String email;

    @Column(name = "age", nullable = true)
//    @Min(value = 0, message = "Неверно указан возраст")
    private Integer age;

    @NotEmpty(message = "Поле не должно быть пустым")
    @Column(name = "password", nullable = false, length = 64)
//    @Size(min = 4, max = 100, message = "Пароль должен содержать не менее трех символов")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE) //
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Transient
    private String passwordConfirm;

    public User() {
    }

    public User(String username, String firstname, String lastname,
                String email, Integer age, String password) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.age = age;
        this.password = password;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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

    @Override
    public String toString() {
        return "User:" +
                "\nid      = " + id +
                "\nusername= " + username +
                "\npassword= " + password +
                "\nage     = " + age +
                "\nroles   = " + roles;
    }
}