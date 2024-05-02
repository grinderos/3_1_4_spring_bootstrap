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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private RepositoryService repositoryService;

    @Autowired
    public UserDetailsServiceImpl(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {

        User user = repositoryService.findByUsername(username);
        if (user == null){ throw new UsernameNotFoundException(username);}

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return new org.springframework.security.core.userdetails.User
                (user.getUsername(), user.getPassword(), grantedAuthorities);
    }

    //<<<<< delegate repository block >>>>>
    public User findUserById(Long id) {return repositoryService.findUserById(id);}
    public User findByUsername(String username) {
        return repositoryService.findByUsername(username);
    }
    public List<User> getUsers() {return repositoryService.getUsers();}
    public List<Role> getRoles() {return repositoryService.getRoles();}
    public boolean save(User user) {return repositoryService.save(user);}
    public boolean update(User user) {return repositoryService.update(user);}
    public void deleteUserById(Long id) {repositoryService.deleteUserById(id);}
    public void fillUsers() {repositoryService.fillUsers();}
    public void fillRoles() {repositoryService.fillRoles();}
    public void truncate() {repositoryService.truncate();}
    public Role findRoleByName(String name) {return repositoryService.findRoleByName(name);}
    //<<<<< ----- >>>>>

}