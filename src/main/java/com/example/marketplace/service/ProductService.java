package com.example.marketplace.service;

import com.example.marketplace.exception.ProductNotFoundException;
import com.example.marketplace.exception.UserNotFoundException;
import com.example.marketplace.model.Product;
import com.example.marketplace.model.Rating;
import com.example.marketplace.repository.ProductRepository;
import com.example.marketplace.repository.RatingRepository;
import com.example.marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, RatingRepository ratingRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    public List<Product> getAllProducts(String category, BigDecimal minPrice, BigDecimal maxPrice) {
        if (category != null && minPrice != null && maxPrice != null) {
            return productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice);
        }
        if (category != null && minPrice != null) {
            return productRepository.findByCategoryAndPriceGreaterThanEqual(category, minPrice);
        }
        if (category != null && maxPrice != null) {
            return productRepository.findByCategoryAndPriceLessThanEqual(category, maxPrice);
        }
        if (category != null) {
            return productRepository.findByCategory(category);
        }
        if (minPrice != null && maxPrice != null) {
            return productRepository.findByPriceBetween(minPrice, maxPrice);
        }
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(product -> {
            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setPrice(updatedProduct.getPrice());
            product.setCategory(updatedProduct.getCategory());
            return productRepository.save(product);
        });
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public Product rateProduct(Long productId, Long userId, Integer rating) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        Rating existingRating = ratingRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(() -> {
                    Rating newRating = new Rating();
                    newRating.setUserId(userId);
                    newRating.setProductId(productId);
                    return newRating;
                });

        existingRating.setRating(rating);
        ratingRepository.save(existingRating);

        BigDecimal newAverage = ratingRepository.calculateAverageRating(productId)
                .setScale(2, RoundingMode.HALF_UP);

        product.setAverageRating(newAverage);
        return productRepository.save(product);
    }
}
