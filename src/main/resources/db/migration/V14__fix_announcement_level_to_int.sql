-- V14__fix_announcement_level_to_int.sql
-- Konvertiert announcement.level von VARCHAR -> INTEGER (0=info, 1=warn, 2=error)

-- Falls es NULLs gibt, vorab mit "0" füllen, um die USING-Konvertierung zu vereinfachen
UPDATE announcement
SET level = COALESCE(level, '0');

-- Spalte auf INTEGER umstellen, mit robuster USING-Logik:
--  - numerische Strings: direkt casten
--  - textuelle Level: INFO/WARN/ERROR-Mapping
--  - unbekanntes: 0
ALTER TABLE announcement
  ALTER COLUMN level TYPE INTEGER
  USING (
    CASE
      WHEN level ~ '^\s*\d+\s*$' THEN trim(level)::integer
      WHEN upper(trim(level)) IN ('INFO') THEN 0
      WHEN upper(trim(level)) IN ('WARN','WARNING') THEN 1
      WHEN upper(trim(level)) IN ('ERROR','ERR','DANGER','CRITICAL') THEN 2
      ELSE 0
    END
  );

-- NOT NULL + Default setzen (optional, aber sinnvoll für neue Datensätze)
ALTER TABLE announcement
  ALTER COLUMN level SET NOT NULL,
  ALTER COLUMN level SET DEFAULT 0;