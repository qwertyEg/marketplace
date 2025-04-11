package com.example.marketplace.repository;

import com.example.marketplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByCategoryAndPriceBetween(
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    List<Product> findByCategoryAndPriceGreaterThanEqual(String category, BigDecimal minPrice);
    List<Product> findByCategoryAndPriceLessThanEqual(String category, BigDecimal maxPrice);
}