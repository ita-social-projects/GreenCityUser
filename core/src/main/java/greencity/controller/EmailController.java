package greencity.controller;

import greencity.message.PasswordRecoveryMessage;
import greencity.message.UserApprovalMessage;
import greencity.message.VerifyEmailMessage;
import greencity.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Object> sendPasswordRecoveryEmail(PasswordRecoveryMessage message) {
        emailService.sendRestoreEmail(message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping
    public ResponseEntity<Object> sendVerifyEmail(VerifyEmailMessage message) {
        emailService.sendVerificationEmail(message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping
    public ResponseEntity<Object> sendRegistrationApprovalEmail(UserApprovalMessage message) {
        emailService.sendApprovalEmail(message);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
