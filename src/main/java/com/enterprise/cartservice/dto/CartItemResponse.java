package com.enterprise.cartservice.dto;
import lombok.*; import java.math.BigDecimal;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItemResponse {
    private Integer id; private Integer productId; private String productName;
    private BigDecimal productPrice; private Integer quantity; private BigDecimal totalPrice;
}
