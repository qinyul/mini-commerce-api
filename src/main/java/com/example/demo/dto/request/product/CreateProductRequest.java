package com.example.demo.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload to register a new product in the catalog")
public record CreateProductRequest(
        @Schema(description = "Unique SKU or Refrence Code used for inventory tracking", example = "SKU-2024-GM-001", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Code is required") String code,

        @Schema(description = "Detailed marketing description (Markdown supported).", example = "High-performance mechanical keyboard with RGB backlighting and Cherry MX switches.", requiredMode = Schema.RequiredMode.NOT_REQUIRED) String description,

        @Schema(description = "The display name of the product as seen by customers.", example = "Mechanical Gaming Keyboard Pro", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "Name is required") String name) {
}