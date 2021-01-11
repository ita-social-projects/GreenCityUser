package greencity.service.impl;

import greencity.service.EmailService;
import java.util.concurrent.Executors;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

class EmailServiceImplTest {
    private EmailService service;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private ITemplateEngine templateEngine;

    @BeforeEach
    public void setup() {
        initMocks(this);
        service = new EmailServiceImpl(javaMailSender, templateEngine, Executors.newCachedThreadPool(),
            "http://localhost:4200", "http://localhost:4200");

        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @ParameterizedTest
    @CsvSource(value = {"1, Test, test@gmail.com, token, ru",
        "1, Test, test@gmail.com, token, ua",
        "1, Test, test@gmail.com, token, en"})
    void sendVerificationEmail(Long id, String name, String email, String token, String language) {
        service.sendVerificationEmail(id, name, email, token, language);

        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendVerificationEmailIllegalStateException() {
        assertThrows(IllegalStateException.class,
            () -> service.sendVerificationEmail(1L, "Test", "test@gmail.com", "token", "enuaru"));
    }

    @ParameterizedTest
    @CsvSource(value = {"1, Test, test@gmail.com, token",
        "1, Test, test@gmail.com, token",
        "1, Test, test@gmail.com, token"})
    void sendApprovalEmail(Long userId, String name, String email, String token) {
        service.sendApprovalEmail(userId, name, email, token);

        verify(javaMailSender).createMimeMessage();
    }

    @ParameterizedTest
    @CsvSource(value = {"1, Test, test@gmail.com, token, ru",
        "1, Test, test@gmail.com, token, ua",
        "1, Test, test@gmail.com, token, en"})
    public void sendRestoreEmail(Long userId, String userName, String userEmail, String token, String language) {
        service.sendRestoreEmail(userId, userName, userEmail, token, language);

        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendRestoreEmailIllegalStateException() {
        assertThrows(IllegalStateException.class,
            () -> service.sendRestoreEmail(1L, "Test", "test@gmail.com", "token", "enuaru"));
    }
}
