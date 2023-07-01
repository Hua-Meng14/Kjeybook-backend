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
import org.springframework.web.servlet.tags.HtmlEscapeTag;

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
        String requestDate = request.getDateOfRequest().substring(0, 10);;
        String rejectedReason = request.getRejectedReason();
        String bookImg = request.getBook().getBookImg();

        // Build the HTML email content
        StringBuilder htmlContentBuilder = new StringBuilder();
        htmlContentBuilder.append("<!doctype html><html xmlns='http://www.w3.org/1999/xhtml' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:o='urn:schemas-microsoft-com:office:office'><head><title></title><!--[if !mso]><!-- --><meta http-equiv='X-UA-Compatible' content='IE=edge'><!--<![endif]--><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'><style type='text/css'>#outlook a { padding:0; }\n");
        htmlContentBuilder.append(".ReadMsgBody { width:100%; }\n");
        htmlContentBuilder.append(".ExternalClass { width:100%; }\n");
        htmlContentBuilder.append(".ExternalClass * { line-height:100%; }\n");
        htmlContentBuilder.append("body { margin:0;padding:0;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%; }\n");
        htmlContentBuilder.append("table, td { border-collapse:collapse;mso-table-lspace:0pt;mso-table-rspace:0pt; }\n");
        htmlContentBuilder.append("img { border:0;height:auto;line-height:100%; outline:none;text-decoration:none;-ms-interpolation-mode:bicubic; }\n");
        htmlContentBuilder.append("p { display:block;margin:13px 0; }</style><!--[if !mso]><!--><style type='text/css'>@media only screen and (max-width:480px) {\n");
        htmlContentBuilder.append("@-ms-viewport { width:320px; }\n");
        htmlContentBuilder.append("@viewport { width:320px; }\n");
        htmlContentBuilder.append("}</style>\n");
        htmlContentBuilder.append("<link href='https://fonts.googleapis.com/css2?family=Poppins' rel='stylesheet' type='text/css'><style type='text/css'>@import url(https://fonts.googleapis.com/css2?family=Poppins);</style><!--<![endif]--><style type='text/css'>@media only screen and (min-width:480px) {\n");
        htmlContentBuilder.append(".mj-column-per-100 { width:100% !important; max-width: 100%; }\n");
        htmlContentBuilder.append(".mj-column-px-500 { width:500px !important; max-width: 500px; }\n");
        htmlContentBuilder.append("}</style><style type='text/css'>@media only screen and (max-width:480px) {\n");
        htmlContentBuilder.append("table.full-width-mobile { width: 100% !important; }\n");
        htmlContentBuilder.append("td.full-width-mobile { width: auto !important; }\n");
        htmlContentBuilder.append("}</style><style type='text/css'>.poster {\n");
        htmlContentBuilder.append("border-radius: 20px; overflow: clip;\n");
        htmlContentBuilder.append("}</style></head><body><div><div style='background:#ffffff;background-color:#ffffff;Margin:0px auto;max-width:600px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#ffffff;background-color:#ffffff;width:100%;'><tbody><tr><td style='direction:ltr;font-size:0px;padding:0;text-align:center;vertical-align:top;'><!--[if mso | IE]><table role='presentation' border='0' cellpadding='0' cellspacing='0'><![endif]--><!-- header image section --><!--[if mso | IE]><tr><td class='' width='600px' ><table align='center' border='0' cellpadding='0' cellspacing='0' class='' style='width:600px;' width='600' ><tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'><![endif]--><div style='background:#A37551;background-color:#A37551;Margin:0px auto;max-width:600px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#A37551;background-color:#A37551;width:100%;'><tbody><tr><td style='direction:ltr;font-size:0px;padding:20px 0;text-align:center;vertical-align:top;'><!--[if mso | IE]><table role='presentation' border='0' cellpadding='0' cellspacing='0'><tr><td class='' style='vertical-align:top;width:600px;' ><![endif]--><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='vertical-align:top;' width='100%'><tr><td align='center' style='font-size:0px;padding:10px 25px;word-break:break-word;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse:collapse;border-spacing:0px;'><tbody><tr><td style='width:150px;'><img height='auto' src='https://firebasestorage.googleapis.com/v0/b/kjeybook-81ae5.appspot.com/o/bootcamp-logo.png?alt=media&token=d34e1be2-9163-4932-8e48-eb6c4d8d3af2' style='border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;' width='150'></td></tr></tbody></table></td></tr></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table></td></tr><![endif]--><!-- header text section --><!--[if mso | IE]><tr><td class='' width='600px' ><table align='center' border='0' cellpadding='0' cellspacing='0' class='' style='width:600px;' width='600' ><tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'><![endif]--><div style='background:#ffffff;background-color:#ffffff;Margin:0px auto;max-width:600px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#ffffff;background-color:#ffffff;width:100%;'><tbody><tr><td style='direction:ltr;font-size:0px;padding:20px 0;text-align:center;vertical-align:top;'><!--[if mso | IE]><table role='presentation' border='0' cellpadding='0' cellspacing='0'><tr><td class='' style='vertical-align:top;width:600px;' ><![endif]--><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='vertical-align:top;' width='100%'><tr><td align='left' style='font-size:0px;padding:10px 25px;word-break:break-word;'><div style='font-family:Poppins;font-size:20px;font-weight:700;line-height:normal;text-align:left;color:#000000;'>Dear ")
        
        // BORROWER DATA
        .append(borrower)        
        .append(",</div></td></tr><tr><td align='left' style='font-size:0px;padding:10px 25px;word-break:break-word;'><div style='font-family:Poppins;font-size:16px;line-height:normal;text-align:left;color:#000000;'>We regret to inform you that your book rental request has been rejected.</div></td></tr></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table></td></tr><![endif]--><!-- request detail --><!--[if mso | IE]><tr><td class='' width='600px' ><table align='center' border='0' cellpadding='0' cellspacing='0' class='' style='width:600px;' width='600' ><tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'><![endif]--><div style='background:#ffffff;background-color:#ffffff;Margin:0px auto;max-width:600px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#ffffff;background-color:#ffffff;width:100%;'><tbody><tr><td style='direction:ltr;font-size:0px;padding:20px 0;padding-bottom:0px;padding-top:0px;text-align:center;vertical-align:top;'><!--[if mso | IE]><table role='presentation' border='0' cellpadding='0' cellspacing='0'><tr><td class='' style='vertical-align:top;width:600px;' ><![endif]--><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='vertical-align:top;' width='100%'><tr><td align='center' style='font-size:0px;padding:10px 25px;word-break:break-word;'><div style='font-family:Poppins;font-size:20px;font-weight:700;line-height:normal;text-align:center;color:#000000;'>Here is the request detail:</div></td></tr></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table></td></tr><tr><td class='' width='600px' ><table align='center' border='0' cellpadding='0' cellspacing='0' class='' style='width:600px;' width='600' ><tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'><![endif]--><div style='Margin:0px auto;max-width:600px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'><tbody><tr><td style='direction:ltr;font-size:0px;padding:20px 0;padding-bottom:0;padding-left:20px;padding-right:20px;padding-top:5px;text-align:center;vertical-align:top;'><!--[if mso | IE]><table role='presentation' border='0' cellpadding='0' cellspacing='0'><tr><td class='poster-outlook' style='vertical-align:top;width:500px;' ><![endif]--><div class='mj-column-px-500 outlook-group-fix poster' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody><tr><td style='background-color:rgba(208, 180, 159, 0.2);vertical-align:top;padding:20px;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tr><td align='center' style='font-size:0px;padding:10px 25px;word-break:break-word;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse:collapse;border-spacing:0px;'><tbody><tr><td style='width:167px;'><img alt='image cover' height='auto' src=")
        
        // BOOK IMAGE DATA
        .append(bookImg)
        .append("style='border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;' width='167'></td></tr></tbody></table></td></tr><tr><td align='center' style='font-size:0px;padding:10px 25px;word-break:break-word;'><div style='font-family:Poppins;font-size:16px;line-height:normal;text-align:center;color:#000000;'><!-- book title --> <span style='font-weight: 700; font-size: 20px'>")
        
        // BOOK TITLE DATA
        .append(bookTitle)
        .append("</span><br><br><!-- book author --> Author: <span style='font-weight: 700'>")
        
        // BOOK AUTHOR DATA
        .append(author)
        .append("</span></div></td></tr></table></td></tr></tbody></table></div><!--[if mso | IE]></td><td class='' style='vertical-align:top;width:560px;' ><![endif]--><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody><tr><td style='vertical-align:top;padding-top:5px;padding-bottom:5px;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tr><td align='left' style='font-size:0px;padding:10px 25px;word-break:break-word;'><div style='font-family:Poppins;font-size:16px;line-height:normal;text-align:left;color:#000000;'><ul style='margin: 0; padding: 0'><li style='margin: 0 0 1em;\n");

        htmlContentBuilder.append("list-style: disc inside;\n");
        htmlContentBuilder.append("mso-special-format: bullet;'><span style='font-weight: 700'>Request Date</span>: ")
        
        // REQUEST DATE DATA
        .append(requestDate)
        .append("</li><li style='list-style: disc inside;\n");

        htmlContentBuilder.append("mso-special-format: bullet;'><span style='font-weight: 700'>Reject Reason:</span></li></ul></div></td></tr></table></td></tr></tbody></table></div><!--[if mso | IE]></td><td class='poster-outlook' style='vertical-align:top;width:500px;' ><![endif]--><div class='mj-column-px-500 outlook-group-fix poster' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody><tr><td style='background-color:rgba(208, 180, 159, 0.2);vertical-align:top;padding:20px;padding-top:10px;padding-bottom:10px;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tr><td align='left' style='font-size:0px;padding:10px 25px;word-break:break-word;'><div style='font-family:Poppins;font-size:16px;line-height:normal;text-align:left;color:#000000;'>")
        
        // REJECTED REASON DATA
        .append(rejectedReason)
        .append("</div></td></tr></table></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table></td></tr><![endif]--><!-- footer text --><!--[if mso | IE]><tr><td class='' width='600px' ><table align='center' border='0' cellpadding='0' cellspacing='0' class='' style='width:600px;' width='600' ><tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'><![endif]--><div style='Margin:0px auto;max-width:600px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'><tbody><tr><td style='direction:ltr;font-size:0px;padding:20px 0;padding-bottom:0px;text-align:center;vertical-align:top;'><!--[if mso | IE]><table role='presentation' border='0' cellpadding='0' cellspacing='0'><tr><td class='' style='vertical-align:top;width:600px;' ><![endif]--><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody><tr><td style='vertical-align:top;padding-top:0px;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tr><td align='left' style='font-size:0px;padding:10px 25px;word-break:break-word;'><div style='font-family:Poppins;font-size:16px;line-height:normal;text-align:left;color:#000000;'>We apologize for any inconvenience caused. If you have any further questions or concerns, please don't hesitate to reach out to our support team.<br>Thank you for considering our book rental service.</div></td></tr><tr><td align='left' style='font-size:0px;padding:10px 25px;word-break:break-word;'><div style='font-family:Poppins;font-size:16px;line-height:normal;text-align:left;color:#000000;'>Best regards,<br><span style='font-weight: 700'>The Kjey Book Team</span></div></td></tr></table></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table></td></tr><![endif]--><!-- footer --><!--[if mso | IE]><tr><td class='' width='600px' ><table align='center' border='0' cellpadding='0' cellspacing='0' class='' style='width:600px;' width='600' ><tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'><![endif]--><div style='Margin:0px auto;max-width:600px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'><tbody><tr><td style='direction:ltr;font-size:0px;padding:20px 0;padding-top:5px;text-align:center;vertical-align:top;'><!--[if mso | IE]><table role='presentation' border='0' cellpadding='0' cellspacing='0'><tr><td class='' style='vertical-align:top;width:600px;' ><![endif]--><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='vertical-align:top;' width='100%'><tr><td align='center' vertical-align='middle' style='font-size:0px;padding:10px 25px;word-break:break-word;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse:separate;width:200px;line-height:100%;'><tr><td align='center' bgcolor='#A37551' role='presentation' style='border:none;border-radius:15px;cursor:auto;height:30px;padding:10px 25px;background:#A37551;' valign='middle'><a href='https://kjeybook.vercel.app' style='background:#A37551;color:#ffffff;font-family:Poppins;font-size:16px;font-weight:700;line-height:normal;Margin:0;text-decoration:none;text-transform:none;' target='_blank'>EXPLORE MORE</a></td></tr></table></td></tr><tr><td style='font-size:0px;padding:10px 25px;word-break:break-word;'><p style='border-top:solid 2px #A37551;font-size:1;margin:0px auto;width:100%;'></p><!--[if mso | IE]><table align='center' border='0' cellpadding='0' cellspacing='0' style='border-top:solid 2px #A37551;font-size:1;margin:0px auto;width:550px;' role='presentation' width='550px' ><tr><td style='height:0;line-height:0;'> &nbsp;\n");

        htmlContentBuilder.append("</td></tr></table><![endif]--></td></tr></table></div><!--[if mso | IE]></td><td class='' style='vertical-align:top;width:600px;' ><![endif]--><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='vertical-align:top;' width='100%'><tr><td align='center' style='font-size:0px;padding:10px 25px;word-break:break-word;'><!--[if mso | IE]><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' ><tr><td><![endif]--><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='float:none;display:inline-table;'><tr><td style='padding:4px;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#A37551;border-radius:3px;width:40px;'><tr><td style='font-size:0;height:40px;vertical-align:middle;width:40px;'><a href='https://www.facebook.com/sharer/sharer.php?u=https://kjeybook.vercel.app' target='_blank'><img height='40' src='https://www.mailjet.com/images/theme/v1/icons/ico-social/facebook.png' style='border-radius:3px;' width='40'></a></td></tr></table></td></tr></table><!--[if mso | IE]></td><td><![endif]--><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='float:none;display:inline-table;'><tr><td style='padding:4px;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#A37551;border-radius:3px;width:40px;'><tr><td style='font-size:0;height:40px;vertical-align:middle;width:40px;'><a href='https://kjeybook.vercel.app' target='_blank'><img height='40' src='https://www.mailjet.com/images/theme/v1/icons/ico-social/instagram.png' style='border-radius:3px;' width='40'></a></td></tr></table></td></tr></table><!--[if mso | IE]></td><td><![endif]--><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='float:none;display:inline-table;'><tr><td style='padding:4px;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#A37551;border-radius:3px;width:40px;'><tr><td style='font-size:0;height:40px;vertical-align:middle;width:40px;'><a href='https://twitter.com/home?status=https://kjeybook.vercel.app' target='_blank'><img height='40' src='https://www.mailjet.com/images/theme/v1/icons/ico-social/twitter.png' style='border-radius:3px;' width='40'></a></td></tr></table></td></tr></table><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></table></div><!--[if mso | IE]></td><td class='' style='vertical-align:top;width:600px;' ><![endif]--><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'><table border='0' cellpadding='0' cellspacing='0' role='presentation' style='vertical-align:top;' width='100%'><tr><td align='center' style='font-size:0px;padding:10px 25px;word-break:break-word;'><div style='font-family:Poppins;font-size:12px;line-height:normal;text-align:center;color:#000000;'>© 2023 Kjey Book. All rights reserved.</div></td></tr></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></div></body></html>\n");

        String htmlContent = htmlContentBuilder.toString();
        String from = "no-reply@bookrentalsystem.com.kh";
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
