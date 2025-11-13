-- Fügt die von AuditLog erwarteten Spalten hinzu.
-- Idempotent dank IF NOT EXISTS (PostgreSQL >= 9.6).
ALTER TABLE audit_logs
  ADD COLUMN IF NOT EXISTS admin VARCHAR(190);

ALTER TABLE audit_logs
  ADD COLUMN IF NOT EXISTS ip VARCHAR(64);