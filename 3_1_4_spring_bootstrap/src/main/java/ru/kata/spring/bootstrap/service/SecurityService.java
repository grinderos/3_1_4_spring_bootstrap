package ru.kata.spring.bootstrap.service;

public interface SecurityService {
    boolean isAuthenticated();

    void autoLogin(String username, String password);
}