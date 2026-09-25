CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    ville VARCHAR(40) NOT NULL,
    title VARCHAR(45) NOT NULL,
    CHECK (title IN ('ADMIN', 'CLIENT'))
    );