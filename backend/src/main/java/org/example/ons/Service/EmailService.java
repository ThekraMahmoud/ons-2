
        package org.example.ons.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;


    public void sendHtmlEmail(String recipient, String subject, String htmlContent)
            throws Exception {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, StandardCharsets.UTF_8.name());

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            System.out.println("Sending email to: " + recipient);

            mailSender.send(message);

            System.out.println("Email sent successfully to: " + recipient);

        } catch (Exception e) {
            System.err.println("Failed to send email to: " + recipient);
            e.printStackTrace();
            throw e;
        }
    }

}
