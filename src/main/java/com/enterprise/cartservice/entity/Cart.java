package com.enterprise.cartservice.entity;
import jakarta.persistence.*; import lombok.*; import java.util.*;
@Entity @Table(name="carts") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cart {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
    @Column(nullable=false) private String userId;
    @OneToMany(mappedBy="cart",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY)
    @Builder.Default private List<CartItem> items = new ArrayList<>();
}
