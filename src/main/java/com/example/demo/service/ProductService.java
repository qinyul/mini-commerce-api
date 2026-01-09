package com.example.demo.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.request.product.UpdateProductRequest;
import com.example.demo.entity.Product;

public interface ProductService {

    Page<Product> getAllProducts(Pageable pageable);

    Product createProduct(Product product);

    Product updateProduct(UUID id, UpdateProductRequest product);

    void deleteProduct(UUID id);

    Product getById(UUID id);

    boolean isCodeExists(String code);
}
