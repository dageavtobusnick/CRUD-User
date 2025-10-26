CREATE TABLE IF NOT EXISTS usersdata (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    age INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON usersdata(email);

INSERT INTO usersdata (name, email, age) VALUES
    ('John Doe', 'john.doe@example.com', 30),
    ('Jane Smith', 'jane.smith@example.com', 25),
    ('Bob Johnson', 'bob.johnson@example.com', 35)
ON CONFLICT (email) DO NOTHING;