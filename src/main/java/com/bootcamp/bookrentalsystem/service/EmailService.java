package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.model.Request;
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
import java.util.Date;

import static org.hibernate.sql.ast.SqlTreeCreationLogger.LOGGER;

@Service
@Component
@NoArgsConstructor
public class EmailService {
    private final static String EMAIL_CONFIRMATION_SUBJECT = "Book Rental Request Approved";

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendRequestAcceptedEmail(Request request) {

        String sendTo = request.getBorrower().getEmail();
        String borrower = request.getBorrower().getUsername();
        String bookTitle = request.getBook().getTitle();
        String author = request.getBook().getAuthor();
        LocalDate requestDate = request.getDateOfRequest();
        Date returnDate = request.getDateOfReturn();

        // build email
        // send message
        String message = "Dear "+ borrower +",\n" +
                "\n" +
                "We are pleased to inform you that your book rental request has been approved. Here are the details:\n" +
                "\n" +
                "Book Title: "+ bookTitle +"\n" +
                "Author: "+ author +"\n" +
                "Request Date: "+ requestDate +"\n" +
                "Due Date: "+ returnDate +"\n" +
                "\n" +
                "Please make sure to return the book by the due date to avoid any late fees or penalties.\n" +
                "\n" +
                "If you have any questions or need further assistance, feel free to contact our support team.\n" +
                "\n" +
                "Thank you for choosing our book rental service!\n" +
                "\n" +
                "Best regards,\n" +
                "The Book Rental Team" +
                "" +
                "";
        String from = "no-reply@bookrentalsystem.com.kh";
        send(sendTo, from, message);
    }

    @Async
    private void send(String to, String from, String email) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(EMAIL_CONFIRMATION_SUBJECT);
            helper.setText(email);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
