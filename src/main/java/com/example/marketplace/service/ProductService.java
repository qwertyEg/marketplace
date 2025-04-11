package com.example.marketplace.service;

import com.example.marketplace.exception.ProductNotFoundException;
import com.example.marketplace.model.Product;
import com.example.marketplace.model.Rating;
import com.example.marketplace.repository.ProductRepository;
import com.example.marketplace.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          RatingRepository ratingRepository) {
        this.productRepository = productRepository;
        this.ratingRepository = ratingRepository;
    }

    public List<Product> getAllProducts(String category, BigDecimal minPrice, BigDecimal maxPrice) {
        // ... существующая реализация ...
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(product -> {
            // ... существующая реализация ...
        });
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public Product rateProduct(Long productId, Long userId, Integer rating) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        Rating existingRating = ratingRepository.findByUserIdAndProductId(userId, productId)
                .orElse(new Rating(userId, productId, rating));

        existingRating.setRating(rating);
        ratingRepository.save(existingRating);

        BigDecimal newAverage = ratingRepository.calculateAverageRating(productId);
        product.setAverageRating(newAverage);
        return productRepository.save(product);
    }
}