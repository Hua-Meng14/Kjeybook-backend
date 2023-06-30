package com.bootcamp.bookrentalsystem.repository;

import com.bootcamp.bookrentalsystem.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Component
@Qualifier("user")
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetPwdToken(String resetPwdToken);
}
