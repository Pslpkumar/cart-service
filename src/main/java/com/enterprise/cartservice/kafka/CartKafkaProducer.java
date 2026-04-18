package com.enterprise.cartservice.kafka;
import com.enterprise.cartservice.dto.KafkaCartEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
@Component @Slf4j @RequiredArgsConstructor
public class CartKafkaProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    @Value("${kafka.topic.cart-events}") private String topic;
    public void publishCartEvent(KafkaCartEvent event) {
        try {
            String msg = objectMapper.writeValueAsString(event);
            log.info("[KAFKA PRODUCER] Publishing: {}", msg);
            CompletableFuture<SendResult<String,String>> f = kafkaTemplate.send(topic, String.valueOf(event.getCartId()), msg);
            f.whenComplete((r,ex) -> {
                if (ex == null) log.info("[KAFKA PRODUCER] Sent ok");
                else log.error("[KAFKA PRODUCER] Failed: {}", ex.getMessage());
            });
        } catch (Exception e) { log.error("[KAFKA PRODUCER] Error: {}", e.getMessage()); }
    }
}
