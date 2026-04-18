package com.enterprise.cartservice.dto;
import lombok.*; import java.time.LocalDateTime;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> {
    private boolean success; private String message; private T data; private LocalDateTime timestamp;
    public static <T> ApiResponse<T> success(String m, T d) {
        return ApiResponse.<T>builder().success(true).message(m).data(d).timestamp(LocalDateTime.now()).build();
    }
    public static <T> ApiResponse<T> error(String m) {
        return ApiResponse.<T>builder().success(false).message(m).timestamp(LocalDateTime.now()).build();
    }
}
