ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS verification_code_hash VARCHAR(255),
    ADD COLUMN IF NOT EXISTS verification_expires TIMESTAMP,
    ADD COLUMN IF NOT EXISTS verification_attempts INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS last_code_sent TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified);