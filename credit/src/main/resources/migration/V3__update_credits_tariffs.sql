ALTER TABLE credit_tariffs
    ADD COLUMN penalty_rate DECIMAL(5,2) NOT NULL DEFAULT 3.00;

ALTER TABLE credit_tariffs
    ADD COLUMN auto_payment_rate DECIMAL(5,2) NOT NULL DEFAULT 5.00;
