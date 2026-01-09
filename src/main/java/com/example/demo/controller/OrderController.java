package com.example.demo.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.order.CreateOrderRequest;
import com.example.demo.dto.response.order.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Orders", description = "Order lifecycle management (Checkout -> Confirm -> Cancel)")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create/Draft Order", description = "Creates a new order in PENDING status. Validates product existence but does NOT deduct stock yet.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload (empty items, etc)"),
            @ApiResponse(responseCode = "404", description = "One or more products not found")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request) {

        Order order = orderService.createOrder(request.items());
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(order));
    }

    @Operation(summary = "Confrim Order", description = "Triggers the **Inventory Locking**. Deducts stock atomically. Fails if stock is insufficient.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order confirmed and stock deducted"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: Insufficient stock for one or more items")
    })
    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirm(
            @PathVariable UUID id) {

        Order order = orderService.confirmOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(new OrderResponse(order));
    }

    @Operation(summary = "Cancel Order", description = "Cancels a CONFIRMED order and **Restocks** the inventory. Cannot cancel if already shipped (future scope).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled and items restocked"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: Order is not in a cancellable state (must be CONFIRMED)")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(
            @PathVariable UUID id) {

        Order order = orderService.cancelOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(new OrderResponse(order));
    }

    @Operation(summary = "Get Order Details", description = "Retrieves the full order receipt including line items and current status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(
            @PathVariable UUID id) {
        Order order = orderService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new OrderResponse(order));
    }
}
