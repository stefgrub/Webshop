CREATE TABLE IF NOT EXISTS audit_logs (
  id BIGSERIAL PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  admin_username TEXT,
  admin_ip TEXT,
  user_agent TEXT,

  action TEXT NOT NULL,          -- create|update|delete|...
  entity_type TEXT,              -- product|category|user|...
  entity_id TEXT,

  path TEXT,
  request_method TEXT,
  query_string TEXT,
  request_body_masked TEXT,

  diff_before_json TEXT,
  diff_after_json  TEXT
);

CREATE INDEX IF NOT EXISTS idx_audit_created ON audit_logs (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_admin   ON audit_logs (admin_username);
CREATE INDEX IF NOT EXISTS idx_audit_entity  ON audit_logs (entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_action  ON audit_logs (action);