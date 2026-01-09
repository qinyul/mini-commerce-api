package com.example.demo.dto.response.order;

import java.util.List;
import java.util.UUID;
import com.example.demo.entity.Order;
import com.example.demo.entity.enums.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The complete receipt of an order, including its current status and items.")
public record OrderResponse(
                @Schema(description = "Unique Order ID (used for tracking and support).", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") UUID orderId,

                @Schema(description = "Current lifecycle state of the order.", example = "CONFIRMED") OrderStatus status,

                @Schema(description = "List of products purchased in this order.") List<OrderItemResponse> items) {

        public OrderResponse(Order order) {
                this(
                                order.getExternalId(),
                                order.getStatus(),
                                order.getItems().stream()
                                                .map(
                                                                t -> new OrderItemResponse(
                                                                                t.getProduct().getExternalId(),
                                                                                t.getProductCode(),
                                                                                t.getProductName(),
                                                                                t.getQuantity()))
                                                .toList());
        }

}
