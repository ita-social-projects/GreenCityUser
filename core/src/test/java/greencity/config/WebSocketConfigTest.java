package greencity.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import java.util.ArrayList;
import java.util.List;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
    void configureMessageBrokerTest() {
        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);
        webSocketConfig.configureMessageBroker(registry);
        verify(registry).enableSimpleBroker("/topic");
        verify(registry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    void registerStompEndpointsTest() {
        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration registration = mock(StompWebSocketEndpointRegistration.class);

        when(registry.addEndpoint("/socket")).thenReturn(registration);
        when(registration.setAllowedOriginPatterns(any())).thenReturn(registration);

        webSocketConfig.registerStompEndpoints(registry);

        verify(registration).setAllowedOriginPatterns(any());
        verify(registration).withSockJS();
    }

    @Test
    void configureMessageConvertersTest() {
        List<MessageConverter> messageConverters = new ArrayList<>();
        boolean result = webSocketConfig.configureMessageConverters(messageConverters);

        assertFalse(result);
        assertEquals(1, messageConverters.size());

        MessageConverter converter = messageConverters.getFirst();
        assertInstanceOf(MappingJackson2MessageConverter.class, converter);

        MappingJackson2MessageConverter jacksonConverter = (MappingJackson2MessageConverter) converter;
        DefaultContentTypeResolver resolver = (DefaultContentTypeResolver) jacksonConverter.getContentTypeResolver();
        assert resolver != null;
        assertNotNull(resolver);

        assertEquals(MimeTypeUtils.APPLICATION_JSON, resolver.getDefaultMimeType());
    }
}
