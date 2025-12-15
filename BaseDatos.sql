
-- Base de Datos: banking_clients
-- Microservicio: ms-clientes
-- Ejecutar estas sentencias en la base de datos 'banking_clients'

CREATE TABLE IF NOT EXISTS person (
    person_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(20),
    identification VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(200),
    phone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS customer (
    person_id BIGINT PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    status BOOLEAN NOT NULL,
    CONSTRAINT fk_customer_person FOREIGN KEY (person_id) REFERENCES person(person_id)
);

-- Base de Datos: banking_accounts
-- Microservicio: ms-cuentas
-- Ejecutar estas sentencias en la base de datos 'banking_accounts'

CREATE TABLE IF NOT EXISTS account (
    account_id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    account_type VARCHAR(20) NOT NULL,
    initial_balance DECIMAL(15, 2) NOT NULL,
    status BOOLEAN NOT NULL,
    client_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS movement (
    movement_id BIGSERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    movement_type VARCHAR(20) NOT NULL,
    value DECIMAL(15, 2) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    account_id BIGINT NOT NULL,
    CONSTRAINT fk_movement_account FOREIGN KEY (account_id) REFERENCES account(account_id)
);
