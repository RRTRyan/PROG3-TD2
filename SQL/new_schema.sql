\c mini_dish_db;
ALTER TABLE ingredient DROP COLUMN IF EXISTS id_dish;
ALTER TABLE dish ADD COLUMN IF NOT EXISTS price NUMERIC(10,2);

CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');
CREATE TABLE DishIngredient (
    id SERIAL PRIMARY KEY,
    id_dish INT REFERENCES dish(id) NOT NULL,
    id_ingredient INT REFERENCES ingredient(id) NOT NULL,
    quantity_required NUMERIC(10,2) NOT NULL,
    unit UNIT_TYPE NOT NULL
);
