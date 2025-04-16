-- liquibase formatted sql

-- changeset shuml:1744787541549-1
ALTER TABLE device_tokens ADD user_role_on_device VARCHAR(255);

