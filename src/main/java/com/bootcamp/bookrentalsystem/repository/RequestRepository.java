package com.bootcamp.bookrentalsystem.repository;

import com.bootcamp.bookrentalsystem.model.Request;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Component
@Qualifier("request")
@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByBorrowerUserId(Long userId);

    List<Request> findByStatusAndDateOfRequest(String status, LocalDate date);
}
