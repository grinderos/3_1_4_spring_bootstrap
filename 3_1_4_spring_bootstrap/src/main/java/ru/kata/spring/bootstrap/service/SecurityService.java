package ru.kata.spring.bootstrap.service;

import ru.kata.spring.bootstrap.model.Role;

import java.util.List;

public interface SecurityService {
    boolean isAuthenticated();
    void autoLogin(String username, String password);
}