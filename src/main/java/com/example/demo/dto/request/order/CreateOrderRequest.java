package com.example.demo.dto.request.order;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Payload to initiate a new order transaction (Checkout)")
public record CreateOrderRequest(

                @Schema(description = "List of products and quantities to purchase. Must contain at least one item.", requiredMode = Schema.RequiredMode.REQUIRED) @NotEmpty(message = "Order Items is mandatory") @Valid @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY) List<OrderRequestItem> items) {

}
