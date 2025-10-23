-- FKs vorübergehend entfernen (Standardnamen; wenn du eigene Namen hast, sag kurz Bescheid)
ALTER TABLE order_items DROP CONSTRAINT IF EXISTS order_items_order_id_fkey;
ALTER TABLE order_items DROP CONSTRAINT IF EXISTS order_items_product_id_fkey;
ALTER TABLE products     DROP CONSTRAINT IF EXISTS products_category_id_fkey;
ALTER TABLE orders       DROP CONSTRAINT IF EXISTS orders_user_id_fkey;

-- PK-Spalten auf BIGINT
ALTER TABLE users       ALTER COLUMN id TYPE BIGINT;
ALTER TABLE categories  ALTER COLUMN id TYPE BIGINT;
ALTER TABLE products    ALTER COLUMN id TYPE BIGINT;
ALTER TABLE orders      ALTER COLUMN id TYPE BIGINT;
ALTER TABLE order_items ALTER COLUMN id TYPE BIGINT;

-- FK-Spalten auf BIGINT
ALTER TABLE products     ALTER COLUMN category_id TYPE BIGINT USING category_id::bigint;
ALTER TABLE orders       ALTER COLUMN user_id     TYPE BIGINT USING user_id::bigint;
ALTER TABLE order_items  ALTER COLUMN order_id    TYPE BIGINT USING order_id::bigint;
ALTER TABLE order_items  ALTER COLUMN product_id  TYPE BIGINT USING product_id::bigint;

-- FKs wiederherstellen
ALTER TABLE products
  ADD CONSTRAINT products_category_id_fkey FOREIGN KEY (category_id)
  REFERENCES categories(id) ON DELETE SET NULL;

ALTER TABLE orders
  ADD CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id)
  REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE order_items
  ADD CONSTRAINT order_items_order_id_fkey FOREIGN KEY (order_id)
  REFERENCES orders(id) ON DELETE CASCADE;

ALTER TABLE order_items
  ADD CONSTRAINT order_items_product_id_fkey FOREIGN KEY (product_id)
  REFERENCES products(id);
