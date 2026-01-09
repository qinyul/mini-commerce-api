package com.example.demo.dto.request.stock;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "Request to adjust stock quantity")
public record StockAdjustmentRequest(
        @Schema(description = "Quantity adjustment", example = "5", requiredMode = Schema.RequiredMode.REQUIRED) @Min(value = 1, message = "Amount must be at least 1") int amount) {
}
