CREATE TABLE IF NOT EXISTS app_setting (
  id BIGSERIAL PRIMARY KEY,
  key VARCHAR(100) NOT NULL UNIQUE,
  value TEXT NULL,
  updated_at TIMESTAMPTZ DEFAULT now()
);

INSERT INTO app_setting(key,value) VALUES
('maintenance.enabled','false')
ON CONFLICT (key) DO NOTHING;

INSERT INTO app_setting(key,value) VALUES
('maintenance.message','Wartungsarbeiten: Es kann zu Unterbrechungen kommen.')
ON CONFLICT (key) DO NOTHING;

INSERT INTO app_setting(key,value) VALUES
('maintenance.end','')
ON CONFLICT (key) DO NOTHING;

INSERT INTO app_setting(key,value) VALUES
('maintenance.homepageOnly','false')
ON CONFLICT (key) DO NOTHING;