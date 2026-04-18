package com.enterprise.cartservice.dto;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class KafkaCartEvent {
    private Integer cartId; private Integer productId; private Integer quantity;
    private String eventType; private String timestamp;
}
