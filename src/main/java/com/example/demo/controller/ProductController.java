package com.example.demo.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.product.CreateProductRequest;
import com.example.demo.dto.request.product.UpdateProductRequest;
import com.example.demo.dto.response.product.PaginatedResponse;
import com.example.demo.dto.response.product.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Products", description = "Product management APIs")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "List all products", description = "Fetch a paginated list of products. Supports sorting.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping
    public PaginatedResponse<ProductResponse> getAllProducts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Product> pageProduct = productService.getAllProducts(pageable);
        Page<ProductResponse> dtsPage = pageProduct.map(p -> new ProductResponse(
                p.getExternalId(),
                p.getCode(),
                p.getName()));

        return new PaginatedResponse<ProductResponse>(dtsPage);
    }

    @Operation(summary = "Get product by ID", description = "Returns a single product details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ProductResponse geProduct(@PathVariable UUID id) {
        Product p = productService.getById(id);
        return new ProductResponse(
                p.getExternalId(),
                p.getCode(),
                p.getName());

    }

    @Operation(summary = "Create new product", description = "Adds a new product to the catalog")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody CreateProductRequest request) {

        Product product = new Product();
        product.setCode(request.code());
        product.setName(request.name());
        if (request.description() != null) {
            product.setDescription(request.description());
        }

        Product saved = productService.createProduct(product);

        ProductResponse response = new ProductResponse(
                saved.getExternalId(),
                saved.getCode(),
                saved.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update product", description = "Updates an existing product details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id, @RequestBody UpdateProductRequest request) {
        Product updated = productService.updateProduct(id, request);
        return new ProductResponse(
                updated.getExternalId(),
                updated.getCode(),
                updated.getName());
    }

    @Operation(summary = "Delete product", description = "Removes a product from the catalog")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
