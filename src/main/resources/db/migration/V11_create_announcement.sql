CREATE TABLE IF NOT EXISTS announcement (
  id           BIGSERIAL PRIMARY KEY,
  active       BOOLEAN      NOT NULL DEFAULT FALSE,
  level        VARCHAR(16)  NOT NULL,              -- INFO | SUCCESS | WARN | ERROR
  title        VARCHAR(160) NOT NULL,
  message      TEXT         NOT NULL,
  link_url     VARCHAR(512),

  from_ts      TIMESTAMPTZ  NULL,                  -- optionales Fenster: ab
  to_ts        TIMESTAMPTZ  NULL,                  -- optionales Fenster: bis

  audience     VARCHAR(16)  NOT NULL DEFAULT 'ALL',-- ALL | GUEST | USER | ADMIN

  updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_by   VARCHAR(120) NOT NULL DEFAULT 'system'
);

-- sinnvolle Indizes für Abfragen/Filter
CREATE INDEX IF NOT EXISTS ix_announcement_active   ON announcement (active);
CREATE INDEX IF NOT EXISTS ix_announcement_window   ON announcement (from_ts, to_ts);
CREATE INDEX IF NOT EXISTS ix_announcement_level    ON announcement (level);
CREATE INDEX IF NOT EXISTS ix_announcement_audience ON announcement (audience);

-- optional: Demo-Datensatz (kannst du weglassen)
-- INSERT INTO announcement (active, level, title, message, audience)
-- VALUES (true, 'INFO', 'Willkommen!', 'Dies ist eine Demo-Ankündigung.', 'ALL');