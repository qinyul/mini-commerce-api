
package com.example.demo.dto.request.order;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Represents a single line item in the order cart")
public record OrderRequestItem(

                @Schema(description = "The unique ID of the product to purchase", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull(message = "Product ID is mandatory") UUID productId,

                @Schema(description = "Number of units to order. Must be strictly positive.", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED) @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
}