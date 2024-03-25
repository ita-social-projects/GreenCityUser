package greencity.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

public class WebSocketConfigTest {
    private WebSocketConfig webSocketConfig;

    @BeforeEach
    public void setUp() {
        webSocketConfig = new WebSocketConfig();
    }

    @Test
    public void configureMessageBrokerTest() {
        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);
        webSocketConfig.configureMessageBroker(registry);
        verify(registry).enableSimpleBroker("/topic");
        verify(registry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    public void registerStompEndpointsTest() {
        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration registration = mock(StompWebSocketEndpointRegistration.class);

        when(registry.addEndpoint("/socket")).thenReturn(registration);
        when(registration.setAllowedOriginPatterns(any())).thenReturn(registration);

        webSocketConfig.registerStompEndpoints(registry);

        verify(registration).setAllowedOriginPatterns(any());
        verify(registration).withSockJS();
    }

    @Test
    public void configureMessageConvertersTest() {
        List<MessageConverter> messageConverters = new ArrayList<>();
        boolean result = webSocketConfig.configureMessageConverters(messageConverters);
        assert !result;

        assert messageConverters.size() == 1;
        MessageConverter converter = messageConverters.getFirst();
        assert converter instanceof MappingJackson2MessageConverter;

        MappingJackson2MessageConverter jacksonConverter = (MappingJackson2MessageConverter) converter;
        DefaultContentTypeResolver resolver = (DefaultContentTypeResolver) jacksonConverter.getContentTypeResolver();
        assert Objects.equals(Objects.requireNonNull(resolver).getDefaultMimeType(),
            org.springframework.util.MimeTypeUtils.APPLICATION_JSON);
    }
}
