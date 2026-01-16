\c mini_dish_db;
CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TABLE dish (
  id SERIAL PRIMARY KEY,
  "name" VARCHAR(100) NOT NULL,
  dish_type dish_type_enum NOT NULL
);
CREATE TYPE category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
CREATE TABLE ingredient (
  id SERIAL PRIMARY KEY,
  "name" VARCHAR(100) NOT NULL,
  price NUMERIC(10,2) NOT NULL,
  category category_enum NOT NULL,
  id_dish INT REFERENCES dish(id)
);
