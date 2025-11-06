-- ==========================================
--  Flyway Migration: Initial Schema (v1)
--  Compatible with PostgreSQL 16+
-- ==========================================

-- Drop old tables if you reset the DB in dev
-- DO NOT USE IN PRODUCTION!
-- DROP TABLE IF EXISTS order_items, orders, products, categories, users CASCADE;

-- ==========================================
-- USERS
-- ==========================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- ==========================================
-- CATEGORIES
-- ==========================================
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    slug VARCHAR(255) NOT NULL UNIQUE
);

-- ==========================================
-- PRODUCTS
-- ==========================================
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    price_cents INTEGER NOT NULL,
    description TEXT,
    stock INTEGER NOT NULL,
    image_url VARCHAR(1024),
    category_id INTEGER REFERENCES categories(id) ON DELETE SET NULL
);

-- ==========================================
-- ORDERS
-- ==========================================
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_cents INTEGER NOT NULL
);

-- ==========================================
-- ORDER ITEMS
-- ==========================================
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    price_cents INTEGER NOT NULL
);

-- ==========================================
-- INDEXES (optional performance)
-- ==========================================
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

-- ==========================================
-- SAMPLE DATA (optional)
-- ==========================================
INSERT INTO categories (name, slug)
VALUES ('Allgemein', 'allgemein'),
       ('Elektronik', 'elektronik'),
       ('Haushalt', 'haushalt');

INSERT INTO products (name, slug, price_cents, description, stock, image_url, category_id)
VALUES ('Kaffeemaschine', 'kaffeemaschine', 5999, 'Elegante Kaffeemaschine für Zuhause', 10, '/img/kaffeemaschine.jpg', 3),
       ('Smartphone', 'smartphone', 39999, 'Modernes Smartphone mit OLED-Display', 20, '/img/smartphone.jpg', 2);