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

        // Build the HTML email content
        StringBuilder htmlContentBuilder = new StringBuilder();
        htmlContentBuilder.append("<!DOCTYPE html>\n");
        htmlContentBuilder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n");
        htmlContentBuilder.append("<head>\n");
        htmlContentBuilder.append("<title></title>\n");
        htmlContentBuilder.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n");
        htmlContentBuilder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
        htmlContentBuilder.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\" />\n");
        htmlContentBuilder.append("<style type=\"text/css\">#outlook a {padding: 0;} .ReadMsgBody {width: 100%;} .ExternalClass {width: 100%;} .ExternalClass * {line-height: 100%;} body {margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%;} table, td {border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt;} img {border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic;} p {display: block; margin: 13px 0;}</style>\n");
        htmlContentBuilder.append("<style type=\"text/css\">@media only screen and (max-width: 480px) {@-ms-viewport {width: 320px;} @viewport {width: 320px;}}</style>\n");
        htmlContentBuilder.append("<link href=\"https://fonts.googleapis.com/css2?family=Poppins\" rel=\"stylesheet\" type=\"text/css\" />\n");
        htmlContentBuilder.append("<style type=\"text/css\">@import url(https://fonts.googleapis.com/css2?family=Poppins);</style>\n");
        htmlContentBuilder.append("<style type=\"text/css\">@media only screen and (min-width: 480px) {.mj-column-per-100 { width: 100% !important; max-width: 100%;} .mj-column-px-400 {width: 400px !important; max-width: 400px;}}</style>\n");
        htmlContentBuilder.append("<style type=\"text/css\">@media only screen and (max-width: 480px) {table.full-width-mobile {width: 100% !important;} td.full-width-mobile {width: auto !important;}}</style>\n");
        htmlContentBuilder.append("<style type=\"text/css\">.poster {border-radius: 20px; overflow: clip;}</style>\n");
        htmlContentBuilder.append("</head>\n");
        htmlContentBuilder.append("<body>\n");
        htmlContentBuilder.append("<div>\n");
        htmlContentBuilder.append("<div style=\"background: #ffffff; background-color: #ffffff; margin: 0px auto; max-width: 600px;\">");
        htmlContentBuilder.append("<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background: #ffffff; background-color: #ffffff; width: 100%\">");
        htmlContentBuilder.append("<tbody>\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td style=\"direction: ltr; font-size: 0px; padding: 0; text-align: center; vertical-align: top;\">\n");
        htmlContentBuilder.append("<div style=\"background: #a37551; background-color: #a37551; margin: 0px auto; max-width: 600px;\">\n");
        htmlContentBuilder.append("<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background: #a37551; background-color: #a37551; width: 100%;\">\n");
        htmlContentBuilder.append("<tbody>\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td style=\"direction: ltr; font-size: 0px; padding: 20px 0; text-align: center; vertical-align: top;\">\n");
        htmlContentBuilder.append("<div class=\"mj-column-per-100 outlook-group-fix\" style=\"font-size: 13px; text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 100%;\">\n");
        htmlContentBuilder.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align: top\" width=\"100%\">\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td align=\"center\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\">\n");
        htmlContentBuilder.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse: collapse; border-spacing: 0px;\">\n");
        htmlContentBuilder.append("<tbody>\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td style=\"width: 150px\">\n");
        htmlContentBuilder.append("<img height=\"auto\" src=\"https://firebasestorage.googleapis.com/v0/b/kjeybook-81ae5.appspot.com/o/bootcamp-logo.png?alt=media&token=6ca4b3f7-da55-4955-97e5-ddc483f194c0\" style=\"border: 0; display: block; outline: none; text-decoration: none; height: auto; width: 100%;\" width=\"150\" />\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</tbody>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</div>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</tbody>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</div>\n");
        htmlContentBuilder.append("<div style=\"background: #ffffff; background-color: #ffffff; margin: 0px auto; max-width: 600px;\">\n");
        htmlContentBuilder.append("<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background: #ffffff; background-color: #ffffff; width: 100%;\">\n");
        htmlContentBuilder.append("<tbody>\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td style=\"direction: ltr; font-size: 0px; padding: 20px 0; text-align: center; vertical-align: top;\">\n");
        htmlContentBuilder.append("<div class=\"mj-column-per-100 outlook-group-fix\" style=\"font-size: 13px; text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 100%;\">\n");
        htmlContentBuilder.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align: top\" width=\"100%\">\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td align=\"left\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\">\n");
        htmlContentBuilder.append("<div style=\"font-family: Poppins; font-size: 20px; font-weight: 700; line-height: normal; text-align: left; color: #000000;\">\n");
        htmlContentBuilder.append("Dear ").append(request.getBorrower().getUsername()).append(",\n");// DATA BORROW USERNAME
        htmlContentBuilder.append("</div>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td align=\"left\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\">\n");
        htmlContentBuilder.append("<div style=\"font-family: Poppins; font-size: 16px; line-height: normal; text-align: left; color: #000000;\">\n");
        htmlContentBuilder.append("We regret to inform you that your book rental request has been rejected.\n");
        htmlContentBuilder.append("</div>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</div>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</tbody>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</div>\n");
        htmlContentBuilder.append("<div style=\"background: #ffffff; background-color: #ffffff; margin: 0px auto; max-width: 600px;\">\n");
        htmlContentBuilder.append("<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background: #ffffff; background-color: #ffffff; width: 100%;\">\n");
        htmlContentBuilder.append("<tbody>\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td style=\"direction: ltr; font-size: 0px; padding: 20px 0; padding-bottom: 0px; padding-top: 0px; text-align: center; vertical-align: top;\">\n");
        htmlContentBuilder.append("<div class=\"mj-column-per-100 outlook-group-fix\" style=\"font-size: 13px; text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 100%;\">\n");
        htmlContentBuilder.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align: top\" width=\"100%\">\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td align=\"center\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\">\n");
        htmlContentBuilder.append("<div style=\"font-family: Poppins; font-size: 20px; font-weight: 700; line-height: normal; text-align: center; color: #000000;\">Here is the request detail:</div>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</div>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</tbody>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</div>\n");
        htmlContentBuilder.append("<div style=\"margin: 0px auto; max-width: 600px\">\n");
        htmlContentBuilder.append("<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width: 100%\">\n");
        htmlContentBuilder.append("<tbody>\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td style=\"direction: ltr; font-size: 0px; padding: 20px 0; padding-bottom: 0; padding-left: 20px; padding-right: 20px; padding-top: 5px; text-align: center; vertical-align: top;\">\n");
        htmlContentBuilder.append("<div class=\"mj-column-px-400 outlook-group-fix poster\" style=\"font-size: 13px; text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 100%;\">\n");
        htmlContentBuilder.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n");
        htmlContentBuilder.append("<tbody>\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td style=\"background-color: rgba(208,180,159,0.2);vertical-align: top; padding: 20px;\">\n");
        htmlContentBuilder.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td align=\"center\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\">\n");
        htmlContentBuilder.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse: collapse; border-spacing: 0px;\">\n");
        htmlContentBuilder.append("<tbody>\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td style=\"width: 167px\">\n");
        htmlContentBuilder.append("<img alt=\"image cover\" height=\"auto\" src=\"https://firebasestorage.googleapis.com/v0/b/kjeybook-81ae5.appspot.com/o/education%2F1.jpg?alt=media&token=f491da18-0db6-496f-a958-3e8643f94c35\" style=\"border: 0; display: block; outline: none; text-decoration: none; height: auto; width: 100%; \" width=\"167\" />\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</tbody>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("<tr>\n");
        htmlContentBuilder.append("<td align=\"center\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\">\n");
        htmlContentBuilder.append("<div style=\"font-family: Poppins; font-size: 16px; line-height: normal; text-align: center; color: #000000;\">\n");
        htmlContentBuilder.append("<span style=\"font-weight: 700;font-size: 20px;\">")
                  .append(request.getBook().getTitle())
                  .append("</span><br /><br />\n"); // DATA BOOK TITLE
        htmlContentBuilder.append("Author:\n");
        htmlContentBuilder.append("<span style=\"font-weight: 700;\">")
                .append(request.getBook().getAuthor())
                .append("</span>\n");               // DATA BOOK AUTHOR
        htmlContentBuilder.append("</div>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</tbody>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</div>");
        htmlContentBuilder.append("<div class=\"mj-column-per-100 outlook-group-fix\" style=\"font-size: 13px; text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tbody><tr><td style=\"vertical-align: top; padding-top: 5px; padding-bottom: 5px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tr><td align=\"left\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\"><div style=\"font-family: Poppins; font-size: 16px; line-height: normal; text-align: left; color: #000000;\"><ul style=\"margin: 0; padding: 0\"><li style=\"margin: 0 0 1em; list-style: disc inside; mso-special-format: bullet;\"><span style=\"font-weight: 700\">Request Date</span>: ")
        .append(request.getDateOfRequest())         // DATA DATE OF REQUEST
        .append("</li><li style=\"list-style: disc inside; mso-special-format: bullet;\"><span style=\"font-weight: 700\">Reject Reason:</span></li></ul></div></td></tr></table></td></tr></tbody></table></div>\n");
        htmlContentBuilder.append("<div class=\"mj-column-px-400 outlook-group-fix poster\" style=\"font-size: 13px; text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tbody><tr><td style=\"background-color: rgba(208,180,159,0.2); vertical-align: top; padding: 20px; padding-top: 10px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tr><td align=\"left\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\"><div style=\"font-family: Poppins; font-size: 16px; line-height: normal; text-align: left; color: #000000;\">")
        .append(request.getRejectedReason())        // DATA REJECTED REASON
        .append("</div></td></tr></table></td></tr></tbody></table></div>\n");
        htmlContentBuilder.append("</td>\n");
        htmlContentBuilder.append("</tr>\n");
        htmlContentBuilder.append("</tbody>\n");
        htmlContentBuilder.append("</table>\n");
        htmlContentBuilder.append("</div>\n");
        htmlContentBuilder.append("<div style=\"margin: 0px auto; max-width: 600px\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width: 100%\"><tbody><tr><td style=\"direction: ltr; font-size: 0px; padding: 20px 0; padding-bottom: 0px; text-align: center; vertical-align: top;\"><div class=\"mj-column-per-100 outlook-group-fix\" style=\"font-size: 13px; text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tbody><tr><td style=\"vertical-align: top; padding-top: 0px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\"><tr><td align=\"left\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\"><div style=\" font-family: Poppins; font-size: 16px; line-height: normal; text-align: left; color: #000000;\">We apologize for any inconvenience caused. If you have any further questions or concerns, please don't hesitate to reach out to our support team.<br />Thank you for considering our book rental service.</div></td></tr><tr><td align=\"left\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\"><div style=\"font-family: Poppins; font-size: 16px; line-height: normal; text-align: left; color: #000000;\">Best regards,<br /><span style=\"font-weight: 700\">The Kjey Book Team</span></div></td></tr></table></td></tr></tbody></table></div></td></tr></tbody></table></div>\n");
        htmlContentBuilder.append("<div style=\"margin: 0px auto; max-width: 600px\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width: 100%\"><tbody><tr><td style=\"direction: ltr; font-size: 0px; padding: 20px 0; padding-top: 5px; text-align: center; vertical-align: top;\"><div class=\"mj-column-per-100 outlook-group-fix\" style=\"font-size: 13px; text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align: top\" width=\"100%\"><tr><td align=\"center\" vertical-align=\"middle\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse: separate; width: 200px; line-height: 100%;\"><tr><td align=\"center\" bgcolor=\"#A37551\" role=\"presentation\" style=\"border: none; border-radius: 15px; cursor: auto; height: 30px; padding: 10px 25px; background: #a37551;\" valign=\"middle\"><p style=\"background: #a37551; color: #ffffff; font-family: Poppins; font-size: 16px; font-weight: 700; line-height: normal; margin: 0; text-decoration: none; text-transform: none;\">EXPLORE MORE</p></td></tr></table></td></tr><tr><td style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\"><p style=\"border-top: solid 2px #a37551; font-size: 1; margin: 0px auto;width: 100%;\"></p></td></tr></table></div><div class=\"mj-column-per-100 outlook-group-fix\" style=\"font-size: 13px; text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align: top\" width=\"100%\"><tr><td align=\"center\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"float: none; display: inline-table\"><tr><td style=\"padding: 4px\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background: #a37551; border-radius: 3px; width: 40px;\"><tr><td style=\"font-size: 0; height: 40px; vertical-align: middle; width: 40px;\"><a href=\"https://www.facebook.com/sharer/sharer.php?u=[[SHORT_PERMALINK]]\"target=\"_blank\"><img height=\"40\" src=\"https://www.mailjet.com/images/theme/v1/icons/ico-social/facebook.png\" style=\"border-radius: 3px\" width=\"40\" /></a></td></tr></table></td></tr></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"float: none; display: inline-table\"><tr><td style=\"padding: 4px\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background: #a37551; border-radius: 3px; width: 40px;\"><tr><td style=\"font-size: 0; height: 40px; vertical-align: middle; width: 40px;\"><a href=\"[[SHORT_PERMALINK]]\" target=\"_blank\"><img height=\"40\" src=\"https://www.mailjet.com/images/theme/v1/icons/ico-social/instagram.png\" style=\"border-radius: 3px\" width=\"40\" /></a></td></tr></table></td></tr></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"float: none; display: inline-table\"><tr><td style=\"padding: 4px\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background: #a37551; border-radius: 3px; width: 40px;\"><tr><td style=\"font-size: 0; height: 40px; vertical-align: middle; width: 40px;\"><a href=\"https://twitter.com/home?status=[[SHORT_PERMALINK]]\" target=\"_blank\"><img height=\"40\" src=\"https://www.mailjet.com/images/theme/v1/icons/ico-social/twitter.png\" style=\"border-radius: 3px\" width=\"40\" /></a></td></tr></table></td></tr></table></td></tr></table></div><div class=\"mj-column-per-100 outlook-group-fix\" style=\"font-size: 13px; text-align: left; direction: ltr; display: inline-block; vertical-align: top; width: 100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align: top\" width=\"100%\"><tr><td align=\"center\" style=\"font-size: 0px; padding: 10px 25px; word-break: break-word;\"><div style=\"font-family: Poppins; font-size: 12px; line-height: normal; text-align: center; color: #000000;\">\u00A9 2023 Kjey Book. All rights reserved.</div></td></tr></table></div></td></tr></tbody></table></div>\n");
        htmlContentBuilder.append("</td></tr></tbody></table></div></div></body></html>\n");
        // htmlContentBuilder.append("            </ul>\n");
        // htmlContentBuilder.append("            <p>We apologize for any inconvenience caused. If you have any further questions or concerns, please don't hesitate to reach out to our support team.</p>\n");
        // htmlContentBuilder.append("            <p>Thank you for considering our book rental service.</p>\n");
        // htmlContentBuilder.append("            <div class=\"footer\">\n");
        // htmlContentBuilder.append("                <p>Best regards,</p>\n");
        // htmlContentBuilder.append("                <p>The Book Rental Team</p>\n");
        // htmlContentBuilder.append("            </div>\n");
        // htmlContentBuilder.append("        </div>\n");
        // htmlContentBuilder.append("    </div>\n");
        // htmlContentBuilder.append("</body>\n");
        // htmlContentBuilder.append("</html>");
        // htmlContentBuilder.append("                <li><strong>Reason:</strong> ").append(rejectedReason).append("</li>\n");
        // htmlContentBuilder.append("            </ul>\n");
        // htmlContentBuilder.append("            <p>We apologize for any inconvenience caused. If you have any further questions or concerns, please don't hesitate to reach out to our support team.</p>\n");
        // htmlContentBuilder.append("            <p>Thank you for considering our book rental service.</p>\n");
        // htmlContentBuilder.append("            <div class=\"footer\">\n");
        // htmlContentBuilder.append("                <p>Best regards,</p>\n");
        // htmlContentBuilder.append("                <p>The Book Rental Team</p>\n");
        // htmlContentBuilder.append("            </div>\n");
        // htmlContentBuilder.append("        </div>\n");
        // htmlContentBuilder.append("    </div>\n");
        // htmlContentBuilder.append("</body>\n");
        // htmlContentBuilder.append("</html>");
        // htmlContentBuilder.append("                <li><strong>Reason:</strong> ").append(rejectedReason).append("</li>\n");
        // htmlContentBuilder.append("            </ul>\n");
        // htmlContentBuilder.append("            <p>We apologize for any inconvenience caused. If you have any further questions or concerns, please don't hesitate to reach out to our support team.</p>\n");
        // htmlContentBuilder.append("            <p>Thank you for considering our book rental service.</p>\n");
        // htmlContentBuilder.append("            <div class=\"footer\">\n");
        // htmlContentBuilder.append("                <p>Best regards,</p>\n");
        // htmlContentBuilder.append("                <p>The Book Rental Team</p>\n");
        // htmlContentBuilder.append("            </div>\n");
        // htmlContentBuilder.append("        </div>\n");
        // htmlContentBuilder.append("    </div>\n");
        // htmlContentBuilder.append("</body>\n");
        // htmlContentBuilder.append("</html>");
        // htmlContentBuilder.append("                <li><strong>Reason:</strong> ").append(rejectedReason).append("</li>\n");
        // htmlContentBuilder.append("            </ul>\n");
        // htmlContentBuilder.append("            <p>We apologize for any inconvenience caused. If you have any further questions or concerns, please don't hesitate to reach out to our support team.</p>\n");
        // htmlContentBuilder.append("            <p>Thank you for considering our book rental service.</p>\n");
        // htmlContentBuilder.append("            <div class=\"footer\">\n");
        // htmlContentBuilder.append("                <p>Best regards,</p>\n");
        // htmlContentBuilder.append("                <p>The Book Rental Team</p>\n");
        // htmlContentBuilder.append("            </div>\n");
        // htmlContentBuilder.append("        </div>\n");
        // htmlContentBuilder.append("    </div>\n");
        // htmlContentBuilder.append("</body>\n");
        // htmlContentBuilder.append("</html>");
        
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
