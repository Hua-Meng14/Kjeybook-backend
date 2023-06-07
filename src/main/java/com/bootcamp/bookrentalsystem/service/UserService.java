package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("user") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
