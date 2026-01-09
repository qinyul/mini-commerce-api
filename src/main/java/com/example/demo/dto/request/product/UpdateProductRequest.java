package com.example.demo.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload to register a new product in the catalog")
public record UpdateProductRequest(

                @Schema(description = "Update the SKU/Code", example = "SKU-2024-GM-001-REV2", requiredMode = Schema.RequiredMode.NOT_REQUIRED) String code,

                @Schema(description = "New display name", example = "Mechanical Gaming Keyboard Pro (v2)", requiredMode = Schema.RequiredMode.NOT_REQUIRED) String name,

                @Schema(description = "Updated marketing description", example = "Updated definition: Now includes haptic feedback.", requiredMode = Schema.RequiredMode.NOT_REQUIRED) String description) {

        public String getName() {
                return name;
        }

        public String getDescription() {
                return description;
        }

        public String getCode() {
                return code;
        }
}
