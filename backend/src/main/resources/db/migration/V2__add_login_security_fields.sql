-- Campos para seguridad avanzada de login (intentos fallidos y bloqueo temporal)
ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS failed_login_attempts INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS last_failed_login_at TIMESTAMP NULL,
    ADD COLUMN IF NOT EXISTS account_locked_until TIMESTAMP NULL;
