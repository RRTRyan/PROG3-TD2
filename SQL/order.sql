\c mini_dish_db

CREATE TABLE "order" (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(100) NOT NULL UNIQUE,
    creation_datetime TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE SEQUENCE order_reference_seq AS integer
INCREMENT BY 1 START 1;

CREATE FUNCTION GET_ORDER_REFERENCE() RETURNS CHAR(8)
AS 'SELECT CONCAT(''ORD'', REPEAT(''0'', 5 - LENGTH((SELECT nextval(''order_reference_seq''))::VARCHAR)::INT), (SELECT last_value FROM order_reference_seq))'
LANGUAGE SQL
IMMUTABLE;

ALTER TABLE "order" ALTER COLUMN reference SET DEFAULT GET_ORDER_REFERENCE();

CREATE TABLE dishOrder (
    id SERIAL PRIMARY KEY,
    id_order INT REFERENCES "order"(id) NOT NULL,
    id_dish INT REFERENCES Dish(id) NOT NULL,
    quantity INT
);
