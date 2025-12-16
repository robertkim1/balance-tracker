
CREATE TABLE appauth."user" (
                              id UUID PRIMARY KEY,
                              name TEXT NOT NULL,
                              email TEXT NOT NULL UNIQUE,
                              email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                              image TEXT,
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                              updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE appauth.session (
                                 id UUID PRIMARY KEY,
                                 user_id UUID NOT NULL REFERENCES appauth."user"(id) ON DELETE CASCADE,
                                 token TEXT NOT NULL UNIQUE,
                                 expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                 ip_address TEXT,
                                 user_agent TEXT,
                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                                 updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE appauth.account (
                                 id UUID PRIMARY KEY,
                                 user_id UUID NOT NULL REFERENCES appauth."user"(id) ON DELETE CASCADE,
                                 account_id TEXT NOT NULL,
                                 provider_id TEXT NOT NULL,
                                 access_token TEXT,
                                 refresh_token TEXT,
                                 access_token_expires_at TIMESTAMP WITH TIME ZONE,
                                 refresh_token_expires_at TIMESTAMP WITH TIME ZONE,
                                 scope TEXT,
                                 id_token TEXT,
                                 password TEXT,
                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                                 updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE appauth.verification (
                                      id UUID PRIMARY KEY,
                                      identifier TEXT NOT NULL,
                                      value TEXT NOT NULL,
                                      expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                      created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                                      updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

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
