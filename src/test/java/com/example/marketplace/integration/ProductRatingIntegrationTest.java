package com.example.marketplace.integration;

import com.example.marketplace.dto.RatingRequest;
import com.example.marketplace.model.Product;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.ProductRepository;
import com.example.marketplace.repository.RatingRepository;
import com.example.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
public class ProductRatingIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.1")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    private Product savedProduct;
    private User testUser;

    @BeforeEach
    void setup() {
        ratingRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        userRepository.save(testUser);

        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("100.00"));
        product.setCategory("Electronics");
        savedProduct = productRepository.save(product);
    }

    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-User-Id", userId.toString());
        return headers;
    }

    @Test
    void submitAndUpdateRating_ShouldUpdateAverage() {
        Long productId = savedProduct.getId();
        HttpHeaders headers = createHeaders(testUser.getId());

        HttpEntity<RatingRequest> postRequest = new HttpEntity<>(new RatingRequest(4), headers);
        ResponseEntity<String> postResponse = restTemplate.postForEntity(
                "/products/" + productId + "/rate",
                postRequest,
                String.class
        );
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(postResponse.getBody()).contains("\"averageRating\":4.00");

        HttpEntity<RatingRequest> putRequest = new HttpEntity<>(new RatingRequest(5), headers);
        ResponseEntity<String> putResponse = restTemplate.exchange(
                "/products/" + productId + "/rate",
                HttpMethod.PUT,
                putRequest,
                String.class
        );
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResponse.getBody()).contains("\"averageRating\":5.00");
    }

    @Test
    void rateProductWithInvalidValues_ShouldReturnBadRequest() {
        Long productId = savedProduct.getId();
        HttpHeaders headers = createHeaders(testUser.getId());

        HttpEntity<RatingRequest> invalidRequest0 = new HttpEntity<>(new RatingRequest(0), headers);
        ResponseEntity<String> response0 = restTemplate.postForEntity(
                "/products/" + productId + "/rate",
                invalidRequest0,
                String.class
        );
        assertThat(response0.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        HttpEntity<RatingRequest> invalidRequest6 = new HttpEntity<>(new RatingRequest(6), headers);
        ResponseEntity<String> response6 = restTemplate.postForEntity(
                "/products/" + productId + "/rate",
                invalidRequest6,
                String.class
        );
        assertThat(response6.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void rateNonExistingProduct_ShouldReturnNotFound() {
        HttpHeaders headers = createHeaders(testUser.getId());
        HttpEntity<RatingRequest> request = new HttpEntity<>(new RatingRequest(4), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/products/999/rate",
                request,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}