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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public RequestService(@Qualifier("request") RequestRepository requestRepository, UserService userService,
            BookService bookService, UserRepository userRepository, BookRepository bookRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.bookRepository = bookRepository;
    }

    public Request createRequest(UUID userId, UUID bookId, Long requestDuration) {
        User borrower = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Book book = (Book) bookService.findBookById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // Check request duration and maximum request duration for the requested book
        // if (requestDuration > book.getMaximumRequestPeriod()) {
        // throw new BadRequestException("Request Period has exceed maximum request
        // duration for book with id: " + bookId);
        // }

        // Check if there is an existing request with the same userId and bookId
        List<Request> existingRequests = requestRepository.findByBorrowerUserIdAndBookId(userId, bookId);
        if (!existingRequests.isEmpty()) {
            for (Request existingRequest : existingRequests) {
                String existingStatus = existingRequest.getStatus();
                if (existingStatus.equals("PENDING") || existingStatus.equals("ACCEPTED")) {
                    throw new BadRequestException("You have already in the process of requesting this book.");
                }
            }
        }

        // Check if the request book is archived
        if (book.getDeleted()) {
            throw new BadRequestException("Requested book has already been archived.");
        }

        Request request = new Request();
        request.setBorrower(borrower);
        request.setBook(book);
        request.setRequestDuration(requestDuration);
        // Set the dateOfRequest as the current date
        LocalDateTime currentDateTime = LocalDateTime.now().plusHours(7); // convert to ICT
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String currentDateTimeStr = currentDateTime.format(formatter);
        request.setDateOfRequest(currentDateTimeStr);

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
        requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        requestRepository.deleteById(requestId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    public List<Request> getRequestsByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Sort sort = Sort.by(Sort.Direction.DESC, "dateOfRequest");
        return requestRepository.findByBorrowerUserId(userId, sort);
    }

    public Request acceptRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        // Check the request status
        switch (request.getStatus()) {
            case "ACCEPTED":
                throw new BadRequestException("Request has already been accepted.");
            case "ARCHIVED":
                throw new BadRequestException("Request has already been archived.");
            default:
                // Set the accepted date as the current date
                LocalDateTime currentDateTime = LocalDateTime.now().plusHours(7); // convert to ICT
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String currentDateTimeStr = currentDateTime.format(formatter);
                request.setDateOfAccepted(currentDateTimeStr);

                // Set the dateOfReturn based on the request duration
                Long requestDuration = request.getRequestDuration();
                if (requestDuration != null) {
                    LocalDate dateOfReturn = currentDateTime.toLocalDate().plusDays(requestDuration + 1);
                    LocalDateTime dateTimeOfReturn = dateOfReturn.atStartOfDay();
                    String dateOfReturnStr = dateTimeOfReturn.format(formatter);
                    request.setDateOfReturn(dateOfReturnStr);
                }

                // Set the request status to "ACCEPTED"
                request.setStatus("ACCEPTED");
                // Set the request isApproved to true
                request.setIsApproved(true);

                // Set book isRented to "TRUE"
                Optional<Book> optionalBook = bookService.findBookById(request.getBook().getId());
                if (optionalBook.isPresent()) {
                    Book book = optionalBook.get();
                    book.setRented(true);
                }

                // Send email to notify the borrower
                userService.notifyUserRequestAccepted(requestId);

                return requestRepository.save(request);
        }

    }

    public Request rejectRequest(Long requestId, String reason) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        // Check the request status
        switch (request.getStatus()) {
            case "ACCEPTED":
                throw new BadRequestException("Request has already been accepted.");
            case "ARCHIVED":
                throw new BadRequestException("Request has already been archived.");
            default:
                // Set the rejected date as the current date
                LocalDateTime currentDateTime = LocalDateTime.now().plusHours(7); // convert to ICT
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String currentDateTimeStr = currentDateTime.format(formatter);
                request.setDateOfRejected(currentDateTimeStr);

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
    }

    public List<Request> getAllRequests() {
        // Create a Sort object with descending order based on the 'dateOfRequest' field
        Sort sort = Sort.by(Sort.Direction.DESC, "dateOfRequest");
        return requestRepository.findAll(sort);
    }

    public Request returnBook(Long requestId) {
        // Retrieve the request by ID
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with ID: " + requestId));

        // Update the request status to "ARCHIVED"
        request.setStatus("ARCHIVED");

        // Set the dateOfReceived as the current date
        LocalDateTime currentDateTime = LocalDateTime.now().plusHours(7); // convert to ICT
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String currentDateTimeStr = currentDateTime.format(formatter);
        request.setDateOfReceived(currentDateTimeStr);

        // Update the book isRented to false
        UUID requestBookId = request.getBook().getId();
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

    public List<Request> getRequestsByStatusAndDateOfRequest(String status, String date) {

        Sort sort = Sort.by(Sort.Direction.DESC, "dateOfRequest");
        List<Request> requests = requestRepository.findByStatus(status, sort);

        // Filter requests based on date
        List<Request> filteredRequests = new ArrayList<>();
        for (Request request : requests) {
            if (request.getDateOfRequest().contains(date)) {
                filteredRequests.add(request);
            }
        }

        return filteredRequests;
    }

    public List<Request> getRequestsByStatus(String status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "dateOfRequest");
        return requestRepository.findByStatus(status, sort);
    }

    public List<Request> getRequestsByBook(UUID bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        Sort sort = Sort.by(Sort.Direction.DESC, "dateOfRequest");
        return requestRepository.findByBookId(bookId, sort);
    }

    public Map<String, Map<String, Integer>> countRequestsByStatus() {

        List<Request> requests = getAllRequests();

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayString = today.format(formatter);
        String yesterdayString = yesterday.format(formatter);

        Map<String, Map<String, Integer>> data = new HashMap<>();

        Map<String, Integer> pendingMap = new HashMap<>();
        Map<String, Integer> acceptedMap = new HashMap<>();
        Map<String, Integer> archivedMap = new HashMap<>();
        Map<String, Integer> renterMap = new HashMap<>();

        int todayAcceptedCount = 0;
        int yesterdayAcceptedCount = 0;
        int totalAcceptedCount = 0;

        int todayPendingCount = 0;
        int yesterdayPendingCount = 0;
        int totalPendingCount = 0;

        int todayArchivedCount = 0;
        int yesterdayArchivedCount = 0;
        int totalArchivedCount = 0;

        int todayRenterCount = 0;
        int yesterdayRenterCount = 0;
        int totalRenterCount = 0;

        for (Request request : requests) {
            String status = request.getStatus();
            String dateOfRequest = request.getDateOfRequest();

            if (dateOfRequest.contains(todayString)) {
                switch (status) {
                    case "PENDING":
                        todayPendingCount += 1;
                        break;
                    case "ACCEPTED":
                        todayAcceptedCount += 1;
                        break;
                    case "ARCHIVED":
                        if (request.getIsApproved()) {
                            todayRenterCount += 1;
                        }
                        todayArchivedCount += 1;
                        break;
                }

            } else if (dateOfRequest.contains(yesterdayString)) {
                switch (status) {
                    case "PENDING":
                        yesterdayPendingCount += 1;
                        break;
                    case "ACCEPTED":
                        yesterdayAcceptedCount += 1;
                        break;
                    case "ARCHIVED":
                        if (request.getIsApproved()) {
                            yesterdayRenterCount += 1;
                        }
                        yesterdayArchivedCount += 1;
                        break;
                }
            }
            switch (status) {
                case "PENDING":
                    totalPendingCount += 1;
                    break;
                case "ACCEPTED":
                    totalAcceptedCount += 1;
                    break;
                case "ARCHIVED":
                    if (request.getIsApproved()) {
                        totalRenterCount += 1;
                    }
                    totalArchivedCount += 1;
                    break;
            }

        }

        renterMap.put("today", todayRenterCount);
        renterMap.put("yesterday", yesterdayRenterCount);
        renterMap.put("total", totalRenterCount);

        pendingMap.put("today", todayPendingCount);
        pendingMap.put("yesterday", yesterdayPendingCount);
        pendingMap.put("total", totalPendingCount);

        acceptedMap.put("today", todayAcceptedCount);
        acceptedMap.put("yesterday", yesterdayAcceptedCount);
        acceptedMap.put("total", totalAcceptedCount);

        archivedMap.put("today", todayArchivedCount);
        archivedMap.put("yesterday", yesterdayArchivedCount);
        archivedMap.put("total", totalArchivedCount);

        data.put("RENTER", renterMap);
        data.put("PENDING", pendingMap);
        data.put("ACCEPTED", acceptedMap);
        data.put("ARCHIVED", archivedMap);

        return data;
    }
}