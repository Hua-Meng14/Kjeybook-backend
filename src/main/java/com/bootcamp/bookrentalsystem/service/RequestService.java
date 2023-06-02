package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class RequestService {
    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(@Qualifier("request") RequestRepository requestReporitoy) {
        this.requestRepository= requestReporitoy;
    }
}
