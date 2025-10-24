DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='products' AND column_name='image_url'
  ) THEN
    -- Wenn jemand absolute /img/ Pfade in image_url gespeichert hat, strippe Pfad
    UPDATE products
    SET image_url = regexp_replace(image_url, '^(?:/img/|.*[\\/])', '')
    WHERE image_url IS NOT NULL
      AND (image_url LIKE '/img/%' OR image_url ~ '[\\/]');
  END IF;
END $$;