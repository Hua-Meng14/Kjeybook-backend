package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Component
@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("user") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }


    public User updateUserById(Long userId, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()) {
            User user = existingUser.get();
            if (updatedUser.getUsername() != null) {
                user.setUsername(updatedUser.getUsername());
            }
            if (updatedUser.getEmail() != null) {
                user.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPhoneNumber() != null) {
                user.setPhoneNumber(updatedUser.getPhoneNumber());
            }
            if (updatedUser.getProfileImg() != null) {
                user.setProfileImg(updatedUser.getProfileImg());
            }
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
