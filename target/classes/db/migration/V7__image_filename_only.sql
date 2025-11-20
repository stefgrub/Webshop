-- Spalte anlegen, falls noch nicht vorhanden
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='products' AND column_name='image_url'
  ) THEN
    ALTER TABLE products ADD COLUMN image_url VARCHAR(255);
  END IF;
END $$;

-- Dateinamen aus image_url extrahieren (ohne Pfad, inkl. Windows-Backslash)
UPDATE products
SET image_url = regexp_replace(image_url, '^(?:.*[\\/])', '')
WHERE image_url IS NOT NULL AND image_url ~ '[\\/]';

-- Optional absichern:
-- ALTER TABLE products ALTER COLUMN image_filename SET NOT NULL;