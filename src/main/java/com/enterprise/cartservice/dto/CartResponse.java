package com.enterprise.cartservice.dto;
import lombok.*; import java.math.BigDecimal; import java.util.List;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartResponse {
    private Integer cartId; private String userId;
    private List<CartItemResponse> items; private BigDecimal grandTotal;
}
