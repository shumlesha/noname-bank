-- liquibase formatted sql

-- changeset shuml:1740459847971-1
CREATE TABLE user_credentials
(
    id            UUID        NOT NULL,
    email         VARCHAR(64) NOT NULL,
    password_hash VARCHAR(64) NOT NULL,
    CONSTRAINT pk_user_credentials PRIMARY KEY (id)
);

-- changeset shuml:1740459847971-2
ALTER TABLE user_credentials
    ADD CONSTRAINT uc_user_credentials_email UNIQUE (email);

