ALTER TABLE credits
    ADD COLUMN next_payment_date DATE;

ALTER TABLE credits
    ADD COLUMN account_id UUID;