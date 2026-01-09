package com.example.demo.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.product.UpdateProductRequest;
import com.example.demo.entity.Product;
import com.example.demo.exception.EntityAlreadyExistsException;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        // 1. SECURITY: Validate inputs inside the Service
        validateSortField(pageable);
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Product updateProduct(UUID id, UpdateProductRequest request) {
        Product product = getById(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCode(request.getCode());

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = getById(id);
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.existsByCode(product.getCode())) {
            throw new EntityAlreadyExistsException("Product code already exists");
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getById(UUID id) {
        return productRepository.findByExternalId(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCodeExists(String code) {
        return productRepository.existsByCode(code);
    }

    private void validateSortField(Pageable pageable) {
        List<String> allowerdFields = List.of("createdAt", "name", "code", "id");

        pageable.getSort().forEach(order -> {
            if (!allowerdFields.contains(order.getProperty())) {
                throw new IllegalArgumentException("Invalid sort field: " + order.getProperty());
            }
        });
    }
}
