package com.example.demo.dto.request.stock;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to intialize inventory for a product")
public record CreateStockRequest(
        @Schema(description = "The unique of the product to onboard", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull(message = "Product ID Is mandatory") UUID productId,

        @Schema(description = "The unique of the product to onboard", example = "100", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED) @Min(value = 0, message = "Intial quantity cannot be negative") int initialQuantity) {
}
