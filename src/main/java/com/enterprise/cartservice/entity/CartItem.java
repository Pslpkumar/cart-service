package com.enterprise.cartservice.entity;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import lombok.*;
@Entity @Table(name="cart_items") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cart_id",nullable=false) private Cart cart;
    @Column(nullable=false) private Integer productId;
    @NotNull @Min(value=1,message="Quantity must be > 0") @Column(nullable=false) private Integer quantity;
}
