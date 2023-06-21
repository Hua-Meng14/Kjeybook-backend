package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.model.Request;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.RequestRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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
        LocalDate requestDate = request.getDateOfRequest();
        LocalDate returnDate = request.getDateOfReturn();
        LocalDate acceptedDate = request.getDateOfAccepted();

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
                "You can come to get your book starting from " + acceptedDate + " or " + acceptedDate.plusDays(1) + ".\n" +
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
        LocalDate requestDate = request.getDateOfRequest();
        String rejectedReason = request.getRejectedReason();

        // Build the HTML email content
        StringBuilder htmlContentBuilder = new StringBuilder();
        htmlContentBuilder.append("<!DOCTYPE html>\n");
        htmlContentBuilder.append("<html>\n");
        htmlContentBuilder.append("<head>\n");
        htmlContentBuilder.append("    <meta charset=\"UTF-8\">\n");
        htmlContentBuilder.append("    <title>Book Rental Request Rejected</title>\n");
        htmlContentBuilder.append("    <style>\n");
        htmlContentBuilder.append("        /* CSS styling for the email */\n");
        htmlContentBuilder.append("        body {\n");
        htmlContentBuilder.append("            font-family: Arial, sans-serif;\n");
        htmlContentBuilder.append("        }\n");
        htmlContentBuilder.append("        /* Add your CSS styles here */\n");
        htmlContentBuilder.append("    </style>\n");
        htmlContentBuilder.append("</head>\n");
        htmlContentBuilder.append("<body>\n");
        htmlContentBuilder.append("    <div class=\"container\">\n");
        htmlContentBuilder.append("        <div class=\"header\">\n");
        htmlContentBuilder.append("            <h2>Book Rental Request Rejected</h2>\n");
        htmlContentBuilder.append("        </div>\n");
        htmlContentBuilder.append("        <div class=\"content\">\n");
        htmlContentBuilder.append("            <p>Dear <strong>").append(borrower).append("</strong>,</p>\n");
        htmlContentBuilder.append("            <p>We regret to inform you that your book rental request has been rejected. We appreciate your interest, but unfortunately, we are unable to fulfill your request at this time. Here are the details:</p>\n");
        htmlContentBuilder.append("            <ul>\n");
        htmlContentBuilder.append("                <li><strong>Book Title:</strong> ").append(bookTitle).append("</li>\n");
        htmlContentBuilder.append("                <li><strong>Author:</strong> ").append(author).append("</li>\n");
        htmlContentBuilder.append("                <li><strong>Request Date:</strong> ").append(requestDate).append("</li>\n");
        htmlContentBuilder.append("                <li><strong>Reason:</strong> ").append(rejectedReason).append("</li>\n");
        htmlContentBuilder.append("            </ul>\n");
        htmlContentBuilder.append("            <p>We apologize for any inconvenience caused. If you have any further questions or concerns, please don't hesitate to reach out to our support team.</p>\n");
        htmlContentBuilder.append("            <p>Thank you for considering our book rental service.</p>\n");
        htmlContentBuilder.append("            <div class=\"footer\">\n");
        htmlContentBuilder.append("                <p>Best regards,</p>\n");
        htmlContentBuilder.append("                <p>The Book Rental Team</p>\n");
        htmlContentBuilder.append("            </div>\n");
        htmlContentBuilder.append("        </div>\n");
        htmlContentBuilder.append("    </div>\n");
        htmlContentBuilder.append("</body>\n");
        htmlContentBuilder.append("</html>");

        String htmlContent = htmlContentBuilder.toString();

        // build email
        // send message
//        String message = "Dear " + borrower + ",\n" +
//                "\n" +
//                "We regret to inform you that your book rental request has been rejected. We appreciate your interest, but unfortunately, we are unable to fulfill your request at this time. Here are the details:\n" +
//                "\n" +
//                "Book Title: " + bookTitle + "\n" +
//                "Author: " + author + "\n" +
//                "Request Date: " + requestDate + "\n" +
//                "Reason: " + rejectedReason + "\n" +
//                "\n" +
//                "We apologize for any inconvenience caused. If you have any further questions or concerns, please don't hesitate to reach out to our support team.\n" +
//                "\n" +
//                "Thank you for considering our book rental service.\n" +
//                "\n" +
//                "Best regards,\n" +
//                "The Book Rental Team" +
//                "" +
//                "";
        String from = "no-reply@bookrentalsystem.com.kh";
//        send(sendTo, from, message, subject);
        send(sendTo, from, htmlContent, subject);
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
                    new MimeMessageHelper(mimeMessage, true);
//                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(email, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }


}
