CREATE TABLE IF NOT EXISTS PRODUCT (
    product_id BIGINT PRIMARY KEY,
    product_name VARCHAR(255),
    product_description VARCHAR(255),
    product_type VARCHAR(255),
    price DOUBLE,
    quantity BIGINT
);