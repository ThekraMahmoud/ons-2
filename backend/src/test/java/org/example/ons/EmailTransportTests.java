package org.example.ons;

import com.sun.net.httpserver.HttpServer;
import org.example.ons.Service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailTransportTests {
    @Test void hostedMailUsesTheExistingWebhookContractWithoutSmtp() throws Exception {
        var sender = mock(JavaMailSender.class);
        var service = new EmailService(sender);
        var received = new AtomicReference<String>();
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/mail", exchange -> {
            received.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        });
        server.start();
        try {
            ReflectionTestUtils.setField(service, "transport", "webhook");
            ReflectionTestUtils.setField(service, "webhookUrl", "http://127.0.0.1:" + server.getAddress().getPort() + "/mail");
            service.sendHtmlEmail("test@example.invalid", "ONS test", "<p>اختبار</p>");
            var body = new ObjectMapper().readTree(received.get());
            assertEquals("test@example.invalid", body.get("email").asText());
            assertEquals("ONS test", body.get("subject").asText());
            assertEquals("<p>اختبار</p>", body.get("html").asText());
            verifyNoInteractions(sender);
        } finally { server.stop(0); }
    }

    @Test void webhookFailureIsNotReportedAsSuccessfulEmail() throws Exception {
        var sender = mock(JavaMailSender.class);
        var service = new EmailService(sender);
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/mail", exchange -> { exchange.sendResponseHeaders(503, -1); exchange.close(); });
        server.start();
        try {
            ReflectionTestUtils.setField(service, "transport", "webhook");
            ReflectionTestUtils.setField(service, "webhookUrl", "http://127.0.0.1:" + server.getAddress().getPort() + "/mail");
            assertThrows(RestClientException.class, () -> service.sendHtmlEmail("test@example.invalid", "Test", "<p>test</p>"));
            verifyNoInteractions(sender);
        } finally { server.stop(0); }
    }
}
