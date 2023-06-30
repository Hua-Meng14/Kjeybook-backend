package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.model.Request;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hibernate.sql.ast.SqlTreeCreationLogger.LOGGER;

@Service
@Component
@NoArgsConstructor
public class EmailService {
//    private final static String EMAIL_CONFIRMATION_SUBJECT = "Book Rental Request Approved";

    @Autowired
    private JavaMailSender javaMailSender;
    private UserRepository userRepository;

    @Autowired
    public EmailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void sendRequestAcceptedEmail(Request request) {
        String subject = "Book Rental Request Approved";

        String sendTo = request.getBorrower().getEmail();
        String borrower = request.getBorrower().getUsername();
        String bookTitle = request.getBook().getTitle();
        String author = request.getBook().getAuthor();
        String requestDate = request.getDateOfRequest();
        String returnDate = request.getDateOfReturn();
        // String acceptedDate = request.getDateOfAccepted();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDate acceptedDate = LocalDate.parse(request.getDateOfAccepted(), formatter);
        LocalDate nextDay = acceptedDate.plusDays(1);

        // build email
        // send message
        String message = "Dear " + borrower + ",\n" +
                "\n" +
                "We are pleased to inform you that your book rental request has been approved. Here are the details:\n" +
                "\n" +
                "Book Title: " + bookTitle + "\n" +
                "Author: " + author + "\n" +
                "Request Date: " + requestDate + "\n" +
                "Due Date: " + returnDate + "\n" +
                "\n" +
                "You can come to get your book starting from " + acceptedDate + " or " + nextDay + ".\n" +
                "\n" +
                "Please make sure to return the book by the due date to avoid any late fees or penalties.\n" +
                "\n" +
                "If you have any questions or need further assistance, feel free to contact our support team.\n" +
                "\n" +
                "Thank you for choosing our book rental service!\n" +
                "\n" +
                "Best regards,\n" +
                "The Book Rental Team";
        String from = "no-reply@bookrentalsystem.com.kh";
        send(sendTo, from, message, subject);
    }

    public void sendRequestRejectedEmail(Request request) {

        String subject = "Book Rental Request Rejected";

        String sendTo = request.getBorrower().getEmail();
        String borrower = request.getBorrower().getUsername();
        String bookTitle = request.getBook().getTitle();
        String author = request.getBook().getAuthor();
        String requestDate = request.getDateOfRequest();
        String rejectedReason = request.getRejectedReason();

        // build email
        // send message
        String message = "Dear " + borrower + ",\n" +
                "\n" +
                "We regret to inform you that your book rental request has been rejected. We appreciate your interest, but unfortunately, we are unable to fulfill your request at this time. Here are the details:\n" +
                "\n" +
                "Book Title: " + bookTitle + "\n" +
                "Author: " + author + "\n" +
                "Request Date: " + requestDate + "\n" +
                "Reason: " + rejectedReason + "\n" +
                "\n" +
                "We apologize for any inconvenience caused. If you have any further questions or concerns, please don't hesitate to reach out to our support team.\n" +
                "\n" +
                "Thank you for considering our book rental service.\n" +
                "\n" +
                "Best regards,\n" +
                "The Book Rental Team" +
                "" +
                "";
        String from = "no-reply@bookrentalsystem.com.kh";
        send(sendTo, from, message, subject);
    }

    public void sendResetPasswordEmail(String email, String resetPwdToken, LocalDateTime expirationTime) {

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String subject = "Password Reset Request";
        String sendTo = existingUser.getEmail();
        String resetLink = "https://example.com/reset-password?email=" + existingUser.getEmail() + "&token=" + resetPwdToken;

        // build email
        // send message
        String message = "Hello " + existingUser.getUsername() + ",\n\n" +
                "We have received a request to reset your password for your account. If you did not initiate this request, please ignore this email.\n\n" +
                "To reset your password, click on the following link:\n" +
                resetLink + "\n\n" +
                "Please note that this link will expire in " + expirationTime + ". If you do not reset your password within this timeframe, you will need to submit another password reset request.\n\n" +
                "If you have any questions or need further assistance, please contact our support team at bookrentalsystem.kit@gmail.com.\n\n" +
                "Thank you,\n" +
                "The Book Rental Team";
        String from = "no-reply@bookrentalsystem.com.kh";
        send(sendTo, from, message, subject);
    }

    public void sendResetPasswordSuccessEmail(User user) {
        String subject = "Password Reset Success";
        String sendTo = user.getEmail();
        String loginLink = "https://example.com/login";

// Build email
        String message = "Hello " + user.getUsername() + ",\n\n" +
                "Your password has been successfully reset. You can now log in using your new password.\n\n" +
                "To log in, click on the following link:\n" +
                loginLink + "\n\n" +
                "If you have any questions or need further assistance, please contact our support team at bookrentalsystem.kit@gmail.com.\n\n" +
                "Thank you,\n" +
                "The Book Rental Team";
        String from = "no-reply@bookrentalsystem.com.kh";
        send(sendTo, from, message, subject);
    }

    @Async
    private void send(String to, String from, String email, String subject) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(email);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }


}
