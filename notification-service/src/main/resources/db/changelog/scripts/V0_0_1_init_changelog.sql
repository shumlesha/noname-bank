-- liquibase formatted sql

-- changeset shuml:1744778983059-1
CREATE TABLE device_tokens (id UUID NOT NULL, token VARCHAR(1024) NOT NULL, user_id UUID NOT NULL, device_type VARCHAR(255), created_at TIMESTAMP WITHOUT TIME ZONE, updated_at TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT pk_device_tokens PRIMARY KEY (id));

-- changeset shuml:1744778983059-2
ALTER TABLE device_tokens ADD CONSTRAINT uc_device_tokens_token UNIQUE (token);

