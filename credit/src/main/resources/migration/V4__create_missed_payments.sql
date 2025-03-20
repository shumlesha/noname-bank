CREATE TABLE missed_payments (
       id UUID PRIMARY KEY,
       credit_id UUID NOT NULL,
       missed_date DATE NOT NULL,
       debt NUMERIC(19, 2) NOT NULL,
       amount NUMERIC(19, 2),
       CONSTRAINT fk_credit FOREIGN KEY (credit_id) REFERENCES credits(id) ON DELETE CASCADE
);