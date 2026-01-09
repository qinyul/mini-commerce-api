package com.example.demo.dto.response.order;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Details of a specific product within a confirmed order (Line Item)")
public record OrderItemResponse(

                @Schema(description = "The unique identifier of the procuct", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") UUID productId,

                @Schema(description = "The SKU or reference code of the product.", example = "SKU-2024-GM-001") String productCode,

                @Schema(description = "The display name of the product.", example = "Mechanical Gaming Keyboard Pro") String productName,

                @Schema(description = "The quantity purchased.", example = "2") int quantity) {

}
