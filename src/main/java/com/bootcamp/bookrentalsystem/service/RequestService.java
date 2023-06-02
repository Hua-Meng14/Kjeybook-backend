package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.model.Request;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final BookService bookService;
    private final UserService userService;

    @Autowired
    public RequestService(@Qualifier("request") RequestRepository requestReporitoy, UserService userService, BookService bookService) {
        this.requestRepository= requestReporitoy;
        this.bookService = bookService;
        this.userService = userService;

    }

    public Request createRequest(Long userId, Long bookId, Long requestDuration) {
        User borrower = userService.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Book book = (Book) bookService.findBookById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        Request request = new Request();
        request.setBorrower(borrower);
        request.setBook(book);
        request.setStatus("Pending");
        request.setRequestDuration(requestDuration);
        request.setDateOfAccepted(null);
        request.setDateOfReturn(null);

        return requestRepository.save(request);
    }
}
