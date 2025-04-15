ALTER TABLE products
    ADD COLUMN average_rating DECIMAL(3,2) DEFAULT 0.00
    CONSTRAINT average_rating_range CHECK (average_rating BETWEEN 0 AND 5);

CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, product_id)
);

CREATE INDEX idx_ratings_product_id ON ratings(product_id);