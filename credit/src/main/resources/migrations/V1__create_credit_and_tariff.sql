CREATE TABLE credit_tariffs (
       id UUID PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       interest_rate DECIMAL(10, 4) NOT NULL
);

CREATE TABLE credits (
       id UUID PRIMARY KEY,
       client_id UUID NOT NULL,
       amount DECIMAL(15, 2) NOT NULL,
       paid_amount DECIMAL(15, 2) DEFAULT 0 NOT NULL,
       tariff_id UUID,
       status VARCHAR(50) NOT NULL,
       FOREIGN KEY (tariff_id) REFERENCES credit_tariffs(id) ON DELETE SET NULL
);