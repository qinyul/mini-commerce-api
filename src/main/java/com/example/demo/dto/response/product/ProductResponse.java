package com.example.demo.dto.response.product;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Publicly available product details for catalog listings")
public record ProductResponse(

        @Schema(description = "The unique external ID. Use this for navigation (e.g. /products/{id}) and API calls.", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") UUID id,

        @Schema(description = "Unique Stock Keeping Unit (SKU) or reference code.", example = "SKU-2024-GM-001") String code,

        @Schema(description = "The official display name of the product.", example = "Mechanical Gaming Keyboard Pro") String name) {
}