CREATE TYPE stock_movement_enum AS ENUM ('IN', 'OUT');
CREATE TABLE stock_movement (
  id SERIAL PRIMARY KEY,
  id_ingredient INT REFERENCES ingredient(id) NOT NULL,
  quantity NUMERIC(10,2) NOT NULL,
  unit UNIT_TYPE NOT NULL,
  type stock_movement_enum NOT NULL,
  creation_datetime TIMESTAMP NOT NULL DEFAULT NOW()
);
