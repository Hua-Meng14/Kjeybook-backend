package com.bootcamp.bookrentalsystem.repository;

import com.bootcamp.bookrentalsystem.model.Request;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Qualifier("request")
@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByBorrowerUserId(UUID userId, Sort sort);
    List<Request> findByStatusAndDateOfRequest(String status, LocalDateTime startTime, Sort sort);
    List<Request> findByStatus(String status, Sort sort);
    List<Request> findByBookId(UUID bookId, Sort sort);
    List<Request> findByBorrowerUserIdAndBookId(UUID userId, UUID bookId);
}
