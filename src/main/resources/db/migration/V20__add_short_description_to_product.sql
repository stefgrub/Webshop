-- Neue Kurzbeschreibung-Spalte für Produkte
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS short_description VARCHAR(500);