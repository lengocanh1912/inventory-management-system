package com.ims.dto.response;
import com.ims.enums.MovementType;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MovementResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private MovementType type;
    private Integer quantity;
    private Integer stockBefore;
    private Integer stockAfter;
    private String note;
    private String performedBy;
    private LocalDateTime createdAt;
}
