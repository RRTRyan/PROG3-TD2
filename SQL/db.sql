CREATE DATABASE mini_dish_db;
CREATE USER mini_dish_db_manager WITH LOGIN PASSWORD '';
\c mini_dish_db;
GRANT CONNECT, CREATE ON DATABASE mini_dish_db TO USER mini_dish_db_manager;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT INSERT, SELECT, DELETE, UPDATE ON TABLES TO mini_dish_db_manager;
