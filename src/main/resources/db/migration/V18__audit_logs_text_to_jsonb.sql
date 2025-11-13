-- V18__audit_logs_text_to_jsonb.sql
-- Konvertiert TEXT -> JSONB für die drei JSON-Felder
-- robust gegen NULL/Leerstring und bereits vorhandenen JSON/JSONB-Typ

DO $$
BEGIN
    -- request_body_masked
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'audit_logs' AND column_name = 'request_body_masked'
    ) THEN
        BEGIN
            ALTER TABLE audit_logs
                ALTER COLUMN request_body_masked TYPE JSONB
                USING CASE
                         WHEN request_body_masked IS NULL OR btrim(request_body_masked) = '' THEN NULL
                         ELSE request_body_masked::jsonb
                     END;
        EXCEPTION WHEN others THEN
            -- Falls es schon JSON/JSONB ist, ignorieren
            NULL;
        END;
    ELSE
        ALTER TABLE audit_logs ADD COLUMN request_body_masked JSONB;
    END IF;

    -- diff_before_json
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'audit_logs' AND column_name = 'diff_before_json'
    ) THEN
        BEGIN
            ALTER TABLE audit_logs
                ALTER COLUMN diff_before_json TYPE JSONB
                USING CASE
                         WHEN diff_before_json IS NULL OR btrim(diff_before_json) = '' THEN NULL
                         ELSE diff_before_json::jsonb
                     END;
        EXCEPTION WHEN others THEN
            NULL;
        END;
    ELSE
        ALTER TABLE audit_logs ADD COLUMN diff_before_json JSONB;
    END IF;

    -- diff_after_json
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'audit_logs' AND column_name = 'diff_after_json'
    ) THEN
        BEGIN
            ALTER TABLE audit_logs
                ALTER COLUMN diff_after_json TYPE JSONB
                USING CASE
                         WHEN diff_after_json IS NULL OR btrim(diff_after_json) = '' THEN NULL
                         ELSE diff_after_json::jsonb
                     END;
        EXCEPTION WHEN others THEN
            NULL;
        END;
    ELSE
        ALTER TABLE audit_logs ADD COLUMN diff_after_json JSONB;
    END IF;
END $$;