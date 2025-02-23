-- liquibase formatted sql

-- changeset shuml:1739980262809-1
CREATE TABLE roles
(
    id          UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    banned      BOOLEAN      NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

-- changeset shuml:1739980262809-2
CREATE TABLE user_roles
(
    role_id UUID NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

-- changeset shuml:1739980262809-3
CREATE TABLE users
(
    id        UUID         NOT NULL,
    full_name VARCHAR(50)  NOT NULL,
    email     VARCHAR(64)  NOT NULL,
    gender    VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset shuml:1739980262809-4
ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

-- changeset shuml:1739980262809-5
ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

-- changeset shuml:1739980262809-6
ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

-- changeset shuml:1739980262809-7
ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset shuml:1739980262809-8
ALTER TABLE roles
    ADD CONSTRAINT name_check CHECK (name ~ '^[a-zA-Z\s]*$');

-- changeset shuml:1739980262809-9
ALTER TABLE roles
    ADD CONSTRAINT description_check CHECK (description ~ '^[a-zA-ZА-Яа-я\s]*$');

-- changeset shuml:1739980262809-10
ALTER TABLE users
    ADD CONSTRAINT full_name_check CHECK (full_name ~ '^[a-zA-ZА-Яа-я\s]*$');

-- changeset shuml:1739980262809-11
ALTER TABLE users
    ADD CONSTRAINT email_check CHECK (email ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$');

-- changeset shuml:1739980262809-12
ALTER TABLE users
    ADD CONSTRAINT gender_check CHECK (gender IN ('MALE', 'FEMALE'));
