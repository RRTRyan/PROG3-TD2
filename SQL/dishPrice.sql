ALTER TABLE dish ADD COLUMN IF NOT EXISTS price NUMERIC(10, 2);

UPDATE dish SET price = 2000 WHERE id=1;
UPDATE dish SET price = 6000 WHERE id=2;
UPDATE dish SET price = null WHERE id=3;
UPDATE dish SET price = null WHERE id=4;
UPDATE dish SET price = null WHERE id=5;
