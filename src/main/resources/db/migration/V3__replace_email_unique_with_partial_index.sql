ALTER TABLE users DROP CONSTRAINT IF EXISTS users_email_key;

DROP INDEX IF EXISTS users_email_key;

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email_not_deleted
ON users (email)
WHERE deleted = FALSE;