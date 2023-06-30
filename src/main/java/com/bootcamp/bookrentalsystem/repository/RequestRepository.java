package com.bootcamp.bookrentalsystem.repository;

import com.bootcamp.bookrentalsystem.model.Request;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@Qualifier("request")
@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByBorrowerUserId(UUID userId);

    List<Request> findByStatusAndDateOfRequest(String status, LocalDate date);
    List<Request> findByStatus(String status);
    List<Request> findByBookId(Long bookId);
    List<Request> findByBorrowerUserIdAndBookId(UUID userId, Long bookId);
}
