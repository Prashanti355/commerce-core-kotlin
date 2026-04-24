CREATE TABLE token_blacklist (
    id UUID PRIMARY KEY,
    jti VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    revoked_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    reason TEXT NULL,
    CONSTRAINT fk_token_blacklist_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);