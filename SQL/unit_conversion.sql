\c mini_dish_db;
DELETE FROM stock_movement WHERE type = 'OUT'::stock_movement_enum;

INSERT INTO stock_movement(id, id_ingredient, quantity, type, unit, creation_datetime) VALUES
    (2, 1, 2, 'OUT'::stock_movement_enum, 'PCS'::unit_type, '2024-01-06T12:00'),
    (4, 2, 2, 'OUT'::stock_movement_enum, 'PCS'::unit_type, '2024-01-06T12:00'),
    (6, 3, 1, 'OUT'::stock_movement_enum, 'L'::unit_type, '2024-01-06T13:00'),
    (8, 4, 4, 'OUT'::stock_movement_enum, 'PCS'::unit_type, '2024-01-06T14:00'),
    (10, 5, 1, 'OUT'::stock_movement_enum, 'L'::unit_type, '2024-01-06T14:00');
