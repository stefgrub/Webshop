CREATE TABLE IF NOT EXISTS feature_flag (
  code        VARCHAR(80)  PRIMARY KEY,           -- passt zur Entity @Id code
  enabled     BOOLEAN      NOT NULL DEFAULT FALSE,
  description TEXT,
  updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS ix_feature_flag_enabled ON feature_flag (enabled);

-- optional: ein paar Beispiel-Flags
-- INSERT INTO feature_flag (code, enabled, description) VALUES
--   ('checkoutV2', false, 'Neuer Checkout-Flow'),
--   ('liveSearch', true,  'Live-Suche in der Navbar');