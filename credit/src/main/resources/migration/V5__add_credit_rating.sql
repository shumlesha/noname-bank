CREATE TABLE credit_ratings (
        id UUID PRIMARY KEY,
        client_id UUID NOT NULL,
        rating NUMERIC(5,2) NOT NULL
);