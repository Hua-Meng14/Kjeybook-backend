package com.bootcamp.bookrentalsystem.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.bootcamp.bookrentalsystem.model.Review;

@Component
@Qualifier("review")
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID>{

    List<Review> findByBookId(UUID bookId);

    // boolean existsByUserAndBookId(User existingUser, UUID bookId);
    
}
