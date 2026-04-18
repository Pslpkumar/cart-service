package com.enterprise.cartservice.dto;
import jakarta.validation.constraints.*; import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddToCartRequest {
    @NotNull private Integer productId;
    @NotNull @Min(value=1,message="Quantity must be > 0") private Integer quantity;
    @NotBlank private String userId;
}
