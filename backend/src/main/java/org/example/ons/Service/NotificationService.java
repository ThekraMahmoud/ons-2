package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.Notification;
import org.example.ons.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final RestClient restClient = RestClient.create();

    @Value("${n8n.email-webhook-url}")
    private String emailWebhookUrl;

    @Value("${n8n.whatsapp-webhook-url}")
    private String whatsappWebhookUrl;

    @Value("${ons.email-logo-url}")
    private String logoUrl;

    public void createNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {

        List<Notification> notifications =
                notificationRepository.findAll();

        if (notifications.isEmpty()) {
            throw new ApiException(
                    "there is no notification in the system"
            );
        }

        return notifications;
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(
                        () -> new ApiException("Notification not found")
                );
    }

    public void updateNotification(
            Long id,
            Notification updatedNotification) {

        Notification notification = getNotificationById(id);

        notification.setMessage(updatedNotification.getMessage());
        notification.setStatus(updatedNotification.getStatus());
        notification.setReport(updatedNotification.getReport());
        notification.setCampaign(updatedNotification.getCampaign());

        notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {

        Notification notification = getNotificationById(id);

        notificationRepository.delete(notification);
    }

    public void sendEmail(
            String email,
            String subject,
            String message) {

        if (email == null || email.isBlank()) {
            throw new ApiException("Recipient email is required");
        }

        if (subject == null || subject.isBlank()) {
            throw new ApiException("Email subject is required");
        }

        if (message == null || message.isBlank()) {
            throw new ApiException("Email message is required");
        }

        ClassPathResource template =
                new ClassPathResource("templates/email-template.html");

        String html;

        try (InputStream inputStream = template.getInputStream()) {
            html = new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new ApiException("Could not load email template");
        }

        html = html.replace(
                "{{subject}}",
                HtmlUtils.htmlEscape(subject)
        );

        html = html.replace(
                "{{message}}",
                HtmlUtils.htmlEscape(message)
        );

        html = html.replace(
                "{{logoUrl}}",
                HtmlUtils.htmlEscape(logoUrl)
        );

        Map<String, Object> body = Map.of(
                "email", email,
                "subject", subject,
                "html", html
        );

        try {
            restClient.post()
                    .uri(emailWebhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientException e) {
            throw new ApiException("Could not submit email to n8n");
        }
    }

    public void notifyNewReport(
            AssistanceReport report,
            String email) {

        String locationUrl = "https://www.google.com/maps?q="
                + report.getKiosk().getLatitude()
                + ","
                + report.getKiosk().getLongitude();

        String reportedAt = report.getCreatedAt().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );

        String subject = "بلاغ مساعدة جديد — "
                + report.getCaseNumber();

        String message = """
                %s،

                تم تسجيل بلاغ مساعدة جديد لأحد حجاج حملتكم.

                رقم البلاغ: %s
                اسم الحاج: %s
                موقع الكشك: %s
                وقت البلاغ: %s

                الموقع على الخريطة:
                %s

                يرجى متابعة البلاغ وتكليف أحد موظفي الحملة بالتعامل معه.
                """.formatted(
                report.getCampaign().getCampaignName(),
                report.getCaseNumber(),
                report.getPilgrim().getFullName(),
                report.getKiosk().getLocationName(),
                reportedAt,
                locationUrl
        );

        sendEmail(email, subject, message);
    }

    public void notifyCampaignAdded(
            Campaign campaign,
            String email) {

        String subject = "تم تسجيل الحملة — "
                + campaign.getCampaignName();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String message = """
                تم تسجيل الحملة التالية في نظام أُنس بنجاح.

                اسم الحملة: %s
                تاريخ البداية: %s
                تاريخ النهاية: %s

                نسعد بخدمتكم ورعاية حجاج حملتكم.
                """.formatted(
                campaign.getCampaignName(),
                campaign.getStartDate().format(formatter),
                campaign.getEndDate().format(formatter)
        );

        sendEmail(email, subject, message);
    }

    public void sendWhatsApp(
            String phone,
            String message) {

        if (phone == null || phone.isBlank()) {
            throw new ApiException("Recipient phone is required");
        }

        if (message == null || message.isBlank()) {
            throw new ApiException("WhatsApp message is required");
        }

        String recipient = phone.trim();

        if (recipient.matches("^05\\d{8}$")) {
            recipient = "+966" + recipient.substring(1);
        }

        Map<String, Object> body = Map.of(
                "phone", recipient,
                "message", message
        );

        try {
            restClient.post()
                    .uri(whatsappWebhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientException e) {
            throw new ApiException(
                    "Could not submit WhatsApp message to n8n"
            );
        }
    }

    public void notifyReportAssigned(
            AssistanceReport report,
            String phone) {

        String locationUrl = "https://www.google.com/maps?q="
                + report.getKiosk().getLatitude()
                + ","
                + report.getKiosk().getLongitude();

        String message = """
                تم إسناد بلاغ مساعدة إليك في نظام أُنس.

                رقم البلاغ: %s
                اسم الحاج: %s
                موقع الكشك: %s

                رابط الموقع:
                %s

                يرجى متابعة البلاغ عبر النظام.
                """.formatted(
                report.getCaseNumber(),
                report.getPilgrim().getFullName(),
                report.getKiosk().getLocationName(),
                locationUrl
        );

        sendWhatsApp(phone, message);
    }

    public void notifyOpenReportReminder(
            AssistanceReport report,
            String email) {

        String subject = "تذكير ببلاغ لم يتم قبوله — "
                + report.getCaseNumber();

        String message = """
                %s،

                مضت 7 دقائق على إنشاء البلاغ التالي،
                ولا يزال مفتوحًا ولم يتم قبوله.

                رقم البلاغ: %s
                اسم الحاج: %s
                موقع الكشك: %s

                يرجى تكليف أحد موظفي الحملة بمتابعة البلاغ.
                """.formatted(
                report.getCampaign().getCampaignName(),
                report.getCaseNumber(),
                report.getPilgrim().getFullName(),
                report.getKiosk().getLocationName()
        );

        sendEmail(email, subject, message);
    }
}