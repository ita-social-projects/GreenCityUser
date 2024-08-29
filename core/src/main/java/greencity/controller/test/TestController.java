package greencity.controller.test;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Value("${spring.messaging.stomp.websocket.allowed-origins}")
    protected String[] allowedOrigins;

    @GetMapping
    public ResponseEntity<List<String>> getAllowedOrigins() {
        return ResponseEntity.ok().body(List.of(allowedOrigins));
    }
}
