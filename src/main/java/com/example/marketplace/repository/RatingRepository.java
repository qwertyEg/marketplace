package com.example.marketplace.repository;

import com.example.marketplace.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.math.BigDecimal;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.00) FROM Rating r WHERE r.productId = :productId")
    BigDecimal calculateAverageRating(@Param("productId") Long productId);
}