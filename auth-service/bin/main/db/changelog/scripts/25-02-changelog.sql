-- liquibase formatted sql

-- changeset shuml:1740473233388-1
ALTER TABLE user_credentials
    ADD user_id UUID;

-- changeset shuml:1740473233388-2
ALTER TABLE user_credentials
    ALTER COLUMN user_id SET NOT NULL;

-- changeset shuml:1740473233388-3
ALTER TABLE user_credentials
    ADD CONSTRAINT uc_user_credentials_userid UNIQUE (user_id);

