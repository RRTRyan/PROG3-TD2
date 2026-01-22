INSERT INTO stock_movement(id, id_ingredient, quantity, unit, type, creation_datetime) VALUES
(1, 1, 5.0, "IN"::stock_movement_enum, "KG"::unit_type, "2024-01-05T08:00"),
(2, 1, 0.2, "OUT"::stock_movement_enum, "KG"::unit_type, "2024-01-06T12:00"),
(3, 2, 4.0, "IN"::stock_movement_enum, "KG"::unit_type, "2024-01-05T08:00"),
(4, 2, 0.15, "OUT"::stock_movement_enum, "KG"::unit_type, "2024-01-06T12:00"),
(5, 3, 10.0, "IN"::stock_movement_enum, "KG"::unit_type, "2024-01-04T09:00"),
(6, 3, 1.0, "OUT"::stock_movement_enum, "KG"::unit_type, "2024-01-06T13:00"),
(7, 4, 3.0, "IN"::stock_movement_enum, "KG"::unit_type, "2024-01-05T10:00"),
(8, 4, 0.3, "OUT"::stock_movement_enum, "KG"::unit_type, "2024-01-06T14:00"),
(9, 5, 2.5, "IN"::stock_movement_enum, "KG"::unit_type, "2024-01-05T10:00"),
(10, 5, 0.2, "OUT"::stock_movement_enum, "KG"::unit_type, "2024-01-06T14:00");
