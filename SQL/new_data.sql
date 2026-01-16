\c mini_dish_db;

INSERT INTO dish(id, "name", dish_type, price) VALUES
    (1, 'Salade fraîche', 'START', 3500.00),
    (2, 'Poulet grillé', 'MAIN', 12000.00),
    (3, 'Riz aux légumes', 'MAIN', NULL),
    (4, 'Gâteau au chocolat', 'DESSERT', 8000.00),
    (5, 'Salade de fruits', 'DESSERT', NULL)
ON CONFLICT (id) DO UPDATE
SET "name" = EXCLUDED."name", dish_type = EXCLUDED.dish_type, price = EXCLUDED.price;

INSERT INTO DishIngredient(id, id_dish, id_ingredient, quantity_required, unit) VALUES
    (1, 1, 1, 0.20, 'KG'),
    (2, 1, 2, 0.15, 'KG'),
    (3, 2, 3, 1.00, 'KG'),
    (4, 4, 4, 0.30, 'KG'),
    (5, 4, 5, 0.20, 'KG')
ON CONFLICT (id) DO UPDATE
SET id_dish = EXCLUDED.id_dish, id_ingredient = EXCLUDED.id_ingredient, quantity_required = EXCLUDED.quantity_required, unit = EXCLUDED.unit;
