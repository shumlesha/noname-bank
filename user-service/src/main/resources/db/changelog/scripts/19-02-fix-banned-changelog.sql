-- liquibase formatted sql

-- changeset shuml:1739983119634-1
ALTER TABLE users
    ADD banned BOOLEAN;

-- changeset shuml:1739983119634-2
ALTER TABLE users
    ALTER COLUMN banned SET NOT NULL;

-- changeset shuml:1739983119634-3
ALTER TABLE roles
    DROP COLUMN banned;

