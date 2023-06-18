package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.BadRequestException;
import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.model.Request;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.RequestRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final BookService bookService;
    private final UserService userService;
    private final BookRepository bookRepository;

    @Autowired
    public RequestService(@Qualifier("request") RequestRepository requestRepository, UserService userService, BookService bookService, UserRepository userRepository, BookRepository bookRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.bookRepository = bookRepository;
    }

    public Request createRequest(Long userId, Long bookId, Long requestDuration) {
        User borrower = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Book book = (Book) bookService.findBookById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // Check book renting status
        if (book.getRented()) {
            throw new BadRequestException("Book with id of " + bookId + " is not available for rent!!");
        }

        // Check request duration and maximum request duration for the requested book
        if (requestDuration > book.getMaximumRequestPeriod()) {
            throw new BadRequestException("Request Period has exceed maximum request duration for book with id: " + bookId);
        }


        Request request = new Request();
        request.setBorrower(borrower);
        request.setBook(book);
        request.setRequestDuration(requestDuration);
        // Set the dateOfRequest as the current date
        LocalDate currentDate = LocalDate.now();
        request.setDateOfRequest(currentDate);

        return requestRepository.save(request);
    }

    public Request updateRequestById(Long requestId, Request updatedRequest) {

        Request existingRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        Optional.ofNullable(updatedRequest.getRequestDuration())
                .ifPresent(existingRequest::setRequestDuration);
        Optional.ofNullable(updatedRequest.getStatus())
                .ifPresent(existingRequest::setStatus);
        Optional.ofNullable(updatedRequest.getDateOfAccepted())
                .ifPresent(existingRequest::setDateOfAccepted);
        Optional.ofNullable(updatedRequest.getDateOfReturn())
                .ifPresent(existingRequest::setDateOfReturn);

        return requestRepository.save(existingRequest);
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
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return requestRepository.findByBorrowerUserId(userId);
    }

    public Request acceptRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));


        // Set the accepted date as the current date
        LocalDate currentDate = LocalDate.now();
        request.setDateOfAccepted(currentDate);

        // Set the dateOfReturn based on the request duration
        Long requestDuration = request.getRequestDuration();
        if (requestDuration != null) {
            LocalDate dateOfReturn = currentDate.plusDays(requestDuration + 1);
//            java.sql.Date dateOfReturnWithoutTime = java.sql.Date.valueOf(dateOfReturn);
            request.setDateOfReturn(dateOfReturn);
        }

        // Set the request status to "ACCEPTED"
        request.setStatus("ACCEPTED");
        // Set the request isApproved to true
        request.setIsApproved(true);

        // Set book isRented to "TRUE"
        Optional<Object> requestBook = bookService.findBookById(request.getBook().getId());
        Book book = (Book) requestBook.get();
        book.setRented(true);

        // Send email to notify the borrower
        userService.notifyUserRequestAccepted(requestId);

        return requestRepository.save(request);
    }

    public Request rejectRequest(Long requestId, String reason) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        // Set the rejected date as the current date
        LocalDate currentDate = LocalDate.now();
        request.setDateOfRejected(currentDate);

        // Set the request status to "REJECTED"
        request.setStatus("ARCHIVED");
        // Set the request isApproved to true
        request.setIsApproved(false);
        // Set the rejectedReason
        request.setRejectedReason(reason);

        // Send email to notify the borrower
        userService.notifyUserRequestRejected(requestId);

        return requestRepository.save(request);
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Request returnBook(Long requestId) {
        // Retrieve the request by ID
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));

        // Update the request status to "ARCHIVED"
        request.setStatus("ARCHIVED");

        // Set the dateOfReceived as the current date
        LocalDate currentDate = LocalDate.now();
        request.setDateOfReceived(currentDate);

        // Update the book isRented to false
        Long requestBookId = request.getBook().getId();
        Book book = bookService.getBookById(requestBookId);
        book.setRented(false);

        // Save the updated request
        requestRepository.save(request);
        return request;
    }
    public Request getRequestByRequestId(long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));
        return request;
    }

    public List<Request> getRequestsByStatus(String status) {
        return requestRepository.findByStatus(status);
    }

    public List<Request> getRequestsByBook(Long bookId) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: "+bookId));

        return requestRepository.findByBookId(bookId);
    }
}
