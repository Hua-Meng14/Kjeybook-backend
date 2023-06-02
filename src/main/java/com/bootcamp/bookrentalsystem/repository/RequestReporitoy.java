package com.bootcamp.bookrentalsystem.repository;

import com.bootcamp.bookrentalsystem.model.Request;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Qualifier("request")
@Repository
public interface RequestReporitoy extends JpaRepository<Request, Long> {

}
