CREATE INDEX idx_transaction_user_date ON transaction(user_id, date);

CREATE TABLE datapoint (
                           id UUID PRIMARY KEY,
                           date DATE NOT NULL,
                           balance DECIMAL(10, 2) NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMP
);

CREATE INDEX idx_datapoint_date ON datapoint(date);