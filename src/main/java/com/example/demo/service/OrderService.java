package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import com.example.demo.dto.request.order.OrderRequestItem;
import com.example.demo.entity.Order;

public interface OrderService {

    Order createOrder(List<OrderRequestItem> items);

    Order confirmOrder(UUID orderId);

    Order cancelOrder(UUID orderId);

    Order getById(UUID orderId);
}
