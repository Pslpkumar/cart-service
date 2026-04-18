package com.enterprise.cartservice.dto;
import lombok.*; import java.math.BigDecimal;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDTO {
    private Integer id; private String name; private BigDecimal price;
    private Integer stock; private String description;
}
