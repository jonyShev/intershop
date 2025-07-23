CREATE TABLE IF NOT EXISTS items (
    id IDENTITY PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(1000),
    img_path VARCHAR(255),
    price DOUBLE,
    count INT
);

CREATE TABLE IF NOT EXISTS orders (
    id IDENTITY PRIMARY KEY,
    total_sum DOUBLE
);

CREATE TABLE IF NOT EXISTS order_items (
    id IDENTITY PRIMARY KEY,
    order_id BIGINT,
    item_id BIGINT,
    title VARCHAR(255),
    description VARCHAR(1000),
    img_path VARCHAR(255),
    price DOUBLE,
    count INT
);