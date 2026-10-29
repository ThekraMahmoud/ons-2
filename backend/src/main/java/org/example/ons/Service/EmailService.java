package org.example.ons.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${ons.email.transport:smtp}")
    private String transport = "smtp";

    @Value("${n8n.email-webhook-url:}")
    private String webhookUrl = "";

    public void sendHtmlEmail(String recipient, String subject, String htmlContent) throws Exception {
        if ("webhook".equalsIgnoreCase(transport)) {
            if (webhookUrl == null || webhookUrl.isBlank()) {
                throw new IllegalStateException("Email webhook is not configured");
            }
            RestClient.create().post().uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("email", recipient, "subject", subject, "html", htmlContent))
                    .retrieve().toBodilessEntity();
            return;
        }
        if (!"smtp".equalsIgnoreCase(transport)) {
            throw new IllegalStateException("Unsupported email transport");
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
