CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    actor_email VARCHAR(150) NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NULL,
    status VARCHAR(20) NOT NULL,
    detail TEXT NULL,
    ip_address VARCHAR(100) NULL,
    request_path VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_logs_created_at
ON audit_logs (created_at DESC);

CREATE INDEX idx_audit_logs_actor_email
ON audit_logs (actor_email);

CREATE INDEX idx_audit_logs_entity
ON audit_logs (entity_type, entity_id);