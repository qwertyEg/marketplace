package com.example.marketplace.controller;

import com.example.marketplace.dto.ProductRequest;
import com.example.marketplace.dto.ProductResponse;
import com.example.marketplace.dto.RatingRequest;
import com.example.marketplace.exception.ProductNotFoundException;
import com.example.marketplace.model.Product;
import com.example.marketplace.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(name = "min_price", required = false) BigDecimal minPrice,
            @RequestParam(name = "max_price", required = false) BigDecimal maxPrice) {

        List<Product> products = productService.getAllProducts(category, minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest request) {
        Product product = convertToEntity(request);
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductRequest request) {

        Product productUpdate = convertToEntity(request);
        Optional<Product> updated = productService.updateProduct(id, productUpdate);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rate product", description = "Submit or update product rating (1-5 stars)")
    @ApiResponse(responseCode = "200", description = "Rating submitted")
    @ApiResponse(responseCode = "400", description = "Invalid rating value")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PostMapping("/{id}/rate")
    public ResponseEntity<ProductResponse> rateProduct(
            @PathVariable Long id,
            @RequestBody @Valid RatingRequest request,
            @RequestHeader("X-User-Id") Long userId) {

        Product product = productService.rateProduct(id, userId, request.rating());
        return ResponseEntity.ok(convertToResponse(product));
    }

    private Product convertToEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        return product;
    }

    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getAverageRating());
    }
}