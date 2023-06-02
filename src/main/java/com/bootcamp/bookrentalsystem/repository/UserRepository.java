package com.bootcamp.bookrentalsystem.repository;

import com.bootcamp.bookrentalsystem.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Qualifier("user")
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
