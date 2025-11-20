ALTER TABLE products
    ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE;

-- zur Sicherheit alle bestehenden Produkte aktiv setzen
UPDATE products SET active = TRUE WHERE active IS NULL;