-- V16: Bringt audit_logs in Einklang mit dem AuditLog-Entity

ALTER TABLE audit_logs
  ADD COLUMN IF NOT EXISTS entity                VARCHAR(190),
  ADD COLUMN IF NOT EXISTS entity_id             BIGINT,
  ADD COLUMN IF NOT EXISTS action                VARCHAR(100),
  ADD COLUMN IF NOT EXISTS path                  VARCHAR(512),
  ADD COLUMN IF NOT EXISTS request_method        VARCHAR(20),
  ADD COLUMN IF NOT EXISTS query_string          TEXT,
  ADD COLUMN IF NOT EXISTS user_agent            VARCHAR(512),
  ADD COLUMN IF NOT EXISTS request_body_masked   JSONB,
  ADD COLUMN IF NOT EXISTS diff_before_json      JSONB,
  ADD COLUMN IF NOT EXISTS diff_after_json       JSONB,
  ADD COLUMN IF NOT EXISTS created_at            TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW();

-- optionale Indizes für häufige Filter
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace
                 WHERE c.relname='idx_audit_logs_created_at' AND n.nspname='public') THEN
    CREATE INDEX idx_audit_logs_created_at ON audit_logs (created_at);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace
                 WHERE c.relname='idx_audit_logs_entity' AND n.nspname='public') THEN
    CREATE INDEX idx_audit_logs_entity ON audit_logs (entity, entity_id);
  END IF;
END$$;