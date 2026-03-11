package com.ims.dto.response;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SalesChartResponse {
    private String label;
    private BigDecimal revenue;
    private long orders;
}
