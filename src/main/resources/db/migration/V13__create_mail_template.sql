CREATE TABLE IF NOT EXISTS mail_template (
  code        VARCHAR(80)  PRIMARY KEY,          -- @Id String code
  subject     VARCHAR(255) NOT NULL,
  body_html   TEXT         NOT NULL,
  updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- sinnvolle Indizes (optional)
CREATE INDEX IF NOT EXISTS ix_mail_template_updated_at ON mail_template (updated_at);

-- Optionale Demo-Einträge:
-- INSERT INTO mail_template (code, subject, body_html)
-- VALUES ('order_created', 'Ihre Bestellung wurde erstellt', '<p>Danke für Ihre Bestellung …</p>');