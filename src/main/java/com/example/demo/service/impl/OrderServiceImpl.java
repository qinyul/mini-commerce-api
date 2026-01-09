package com.example.demo.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.order.OrderRequestItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.Stock;
import com.example.demo.entity.enums.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StockRepository;
import com.example.demo.service.OrderService;
import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            StockRepository stockRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public Order createOrder(List<OrderRequestItem> items) {

        List<UUID> ids = items.stream()
                .map(req -> req.productId())
                .sorted()
                .toList();

        List<Product> products = productRepository.findAllByIdsWithLock(ids);

        Map<UUID, Product> productMap = products.stream().collect(Collectors.toMap(Product::getExternalId, p -> p));

        List<OrderItem> orderItems = items.stream().map(req -> {
            Product p = productMap.get(req.productId());

            // Now it looks just like the simple code again
            if (p == null)
                throw new EntityNotFoundException(
                        "Product not found: " + req.productId());

            return new OrderItem(p, req.quantity());
        }).toList();

        Order order = new Order(orderItems);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order confirmOrder(UUID orderId) {
        final Order order = orderRepository.findWithItemsByExternalId(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found: " + orderId));

        List<OrderItem> items = order.getItems();

        List<UUID> productIds = items.stream()
                .map(o -> o.getProduct().getExternalId())
                .sorted()
                .toList();

        List<Stock> stocks = stockRepository.findAllByProductIdsForUpdate(productIds);

        if (stocks.size() != productIds.size()) {
            throw new EntityNotFoundException("Database data inconsistency: Stocks missing for some products");
        }

        Map<UUID, Stock> stockMap = stocks.stream()
                .collect(Collectors.toMap((s) -> s.getProduct().getExternalId(), s -> s));

        for (OrderItem item : items) {

            Stock stock = stockMap.get(item.getProduct().getExternalId());

            stock.decrease(item.getQuantity());
        }

        order.confirm();

        return order;
    }

    @Override
    @Transactional
    public Order cancelOrder(UUID orderId) {
        final Order order = orderRepository.findWithItemsByExternalId(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED orders can be cancelled.");
        }

        List<OrderItem> items = order.getItems();

        List<UUID> productIds = items.stream()
                .map(o -> o.getProduct().getExternalId())
                .sorted()
                .toList();

        List<Stock> stocks = stockRepository.findAllByProductIdsForUpdate(productIds);

        if (stocks.size() != productIds.size()) {
            throw new EntityNotFoundException("Database data inconsistency: Stocks missing for some products");
        }

        Map<UUID, Stock> stockMap = stocks.stream()
                .collect(Collectors.toMap((s) -> s.getProduct().getExternalId(), s -> s));

        for (OrderItem item : items) {

            Stock stock = stockMap.get(item.getProduct().getExternalId());

            stock.increase(item.getQuantity());
        }

        order.cancel();

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getById(UUID orderId) {

        return orderRepository.findWithItemsByExternalId(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found: " + orderId));
    }
}
