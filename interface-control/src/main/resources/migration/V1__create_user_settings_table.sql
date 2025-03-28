CREATE TABLE user_settings (
       user_id UUID PRIMARY KEY,
       theme VARCHAR(255) NOT NULL
);

CREATE TABLE hidden_accounts (
       user_id UUID,
       account_id VARCHAR(255) NOT NULL,
       PRIMARY KEY (user_id, account_id),
       CONSTRAINT fk_user_settings FOREIGN KEY (user_id) REFERENCES user_settings(user_id) ON DELETE CASCADE
);
