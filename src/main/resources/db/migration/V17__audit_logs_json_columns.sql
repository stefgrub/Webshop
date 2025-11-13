-- ==============================================
-- V17__audit_logs_json_columns.sql
-- Fügt JSON- und neue Text-Spalten für Audit-Logs hinzu
-- Kompatibel mit PostgreSQL
-- ==============================================

-- Stelle sicher, dass Tabelle existiert
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY
);

-- Neue Spalten hinzufügen, falls sie noch nicht existieren
ALTER TABLE audit_logs
    ADD COLUMN IF NOT EXISTS admin              VARCHAR(255),
    ADD COLUMN IF NOT EXISTS admin_ip           VARCHAR(255),
    ADD COLUMN IF NOT EXISTS user_agent         TEXT,
    ADD COLUMN IF NOT EXISTS entity             VARCHAR(255),
    ADD COLUMN IF NOT EXISTS entity_id          BIGINT,
    ADD COLUMN IF NOT EXISTS action             VARCHAR(50),
    ADD COLUMN IF NOT EXISTS path               TEXT,
    ADD COLUMN IF NOT EXISTS request_method     VARCHAR(20),
    ADD COLUMN IF NOT EXISTS query_string       TEXT,
    ADD COLUMN IF NOT EXISTS request_body_masked JSONB,
    ADD COLUMN IF NOT EXISTS diff_before_json    JSONB,
    ADD COLUMN IF NOT EXISTS diff_after_json     JSONB,
    ADD COLUMN IF NOT EXISTS created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW();

-- Optional: Indexe für häufige Filterungen
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs (entity);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs (action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_admin ON audit_logs (admin);