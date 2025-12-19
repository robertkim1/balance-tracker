
CREATE TABLE balance.transaction (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    source_name VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    type VARCHAR(20) NOT NULL,
    pay_period VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_transaction_user
                         FOREIGN KEY (user_id)
                         REFERENCES appauth.user(id)
                         ON DELETE CASCADE
);

CREATE INDEX idx_transaction_user_id ON balance.transaction(user_id);
CREATE INDEX idx_transaction_date ON balance.transaction(date);
CREATE INDEX idx_transaction_user_date ON balance.transaction(user_id, date);

CREATE TABLE balance.datapoint (
                           id UUID PRIMARY KEY,
                           date DATE NOT NULL,
                           balance DECIMAL(10, 2) NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMP
);

CREATE INDEX idx_datapoint_date ON balance.datapoint(date);
