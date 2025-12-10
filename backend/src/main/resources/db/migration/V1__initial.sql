CREATE TABLE transaction (
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
                         REFERENCES auth.users(id)
                         ON DELETE CASCADE
);

CREATE INDEX idx_transaction_user_id ON transaction(user_id);
CREATE INDEX idx_transaction_date ON transaction(date);