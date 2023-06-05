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

import java.time.LocalDate;
import java.util.*;

@Component
@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final BookService bookService;
    private final UserService userService;

    @Autowired
    public RequestService(@Qualifier("request") RequestRepository requestReporitoy, UserService userService, BookService bookService) {
        this.requestRepository = requestReporitoy;
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
        request.setStatus("PENDING");
        request.setRequestDuration(requestDuration);
        request.setDateOfAccepted(null);
        request.setDateOfReturn(null);

        // Set the dateOfRequest as the current date
        LocalDate currentDate = LocalDate.now();
        request.setDateOfRequest(currentDate);



        return requestRepository.save(request);
    }

    public Request updateRequestById(Long requestId, Request updatedRequest) {

        Request existingRequst = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        Optional.ofNullable(updatedRequest.getRequestDuration())
                .ifPresent(existingRequst::setRequestDuration);
        Optional.ofNullable(updatedRequest.getStatus())
                .ifPresent(existingRequst::setStatus);
        Optional.ofNullable(updatedRequest.getDateOfAccepted())
                .ifPresent(existingRequst::setDateOfAccepted);
        Optional.ofNullable(updatedRequest.getDateOfReturn())
                .ifPresent(existingRequst::setDateOfReturn);

        return requestRepository.save(existingRequst);
    }

    public Map<String, Boolean> deleteRequestById(Long requestId) {
        Request existingRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        requestRepository.deleteById(requestId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    public List<Request> getRequestsByUserId(Long userId) {
        User existingUser = userService.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return requestRepository.findByBorrowerUserId(userId);
    }

    public Request acceptRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));


        // Set the dateOfRequest as the current date
        LocalDate currentDate = LocalDate.now();
        request.setDateOfAccepted(currentDate);

        // Set the dateOfReturn based on the request duration
        Long requestDuration = request.getRequestDuration();
        if (requestDuration != null) {
            LocalDate dateOfReturn = currentDate.plusDays(requestDuration);
            java.sql.Date dateOfReturnWithoutTime = java.sql.Date.valueOf(dateOfReturn);
            request.setDateOfReturn(dateOfReturnWithoutTime);
        }

        // Set the request status to "ACCEPTED"
        request.setStatus("ACCEPTED");

        // Set book isRented to "TRUE"
        Optional<Object> requestBook = bookService.findBookById(request.getBook().getId());
        Book book = (Book) requestBook.get();
        book.setRented(true);

        // Send email to notify the borrower
        userService.notifyUserRequestAccepted(requestId);

        return requestRepository.save(request);
    }
}
