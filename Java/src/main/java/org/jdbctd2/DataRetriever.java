package org.jdbctd2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private DBConnection dbConnection;

    public DataRetriever(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Dish findByDishId(Integer id) {
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            SELECT d.id AS dishId, d.name AS dishName, dish_type AS dishType, d.price AS dishPrice,
                                    i.id AS ingId, i.name AS ingName, i.price AS price, i.category AS category,
                                    quantity_required, unit
                            FROM dish AS d
                            LEFT JOIN dishingredient ON id_dish = d.id
                            LEFT JOIN ingredient AS i ON id_ingredient = i.id WHERE d.id = ?
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Dish dish = null;
            while (resultSet.next()) {
                if (resultSet.isFirst()) {
                    int dishId = resultSet.getInt("dishId");
                    String dishName = resultSet.getString("dishName");
                    DishTypeEnum dishType = DishTypeEnum.valueOf(resultSet.getString("dishType"));
                    dish = new Dish(dishId, dishName, dishType, new ArrayList<>());
                    if (resultSet.getObject("dishPrice") == null) {
                        dish.setPrice(null);
                    } else {
                        dish.setPrice(resultSet.getDouble("dishPrice"));
                    }
                }

                if (resultSet.getObject("ingId") != null) {
                    int ingredientId = resultSet.getInt("ingId");
                    String ingredientName = resultSet.getString("ingName");
                    double price = resultSet.getDouble("price");
                    CategoryEnum category = CategoryEnum.valueOf(resultSet.getString("category"));
                    Double quantity = resultSet.getDouble("quantity_required");
                    UnitTypeEnum unitType = UnitTypeEnum.valueOf(resultSet.getString("unit"));

                    if (dish != null) {
                        new Ingredient(ingredientId, ingredientName, price, category, dish, quantity, unitType);
                    }
                }
            }
            preparedStatement.close();
            if (dish == null) {
                throw new RuntimeException("Dish not found");
            }
            return dish;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public List<Ingredient> findIngredients(int page, int size) {
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            SELECT i.id AS ingId, i.name AS ingName, i.price, i.category
                                 , d.id AS dishId, d.name AS dishName, dish_type AS dishType
                            FROM ingredient AS i
                            JOIN dishingredient ON i.id = id_ingredient
                            JOIN dish AS d ON d.id = id_dish
                            OFFSET ? LIMIT ?
                            """
            );
            preparedStatement.setInt(1, (page - 1) * size);
            preparedStatement.setInt(2, size);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Ingredient> ingredients = new ArrayList<>();
            createIngredientList(resultSet, ingredients);
            preparedStatement.close();
            connection.close();
            return ingredients;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) throws SQLException {
        Connection connection = dbConnection.getConnection();
        try {
            connection.setAutoCommit(false);

            if (newIngredients.isEmpty()) {
                return newIngredients;
            }

            PreparedStatement ingredientInsertStmt = connection.prepareStatement(
                    "INSERT INTO ingredient VALUES " +
                            "(?, ?, ?, CAST(? AS category_enum)), ".repeat(newIngredients.size() - 1) +
                            "(?, ?, ?, CAST(? AS category_enum))"
            );

            int i = 1;
            for (Ingredient ingredient : newIngredients) {
                ingredientInsertStmt.setInt(i++, ingredient.getId());
                ingredientInsertStmt.setString(i++, ingredient.getName());
                ingredientInsertStmt.setDouble(i++, ingredient.getPrice());
                ingredientInsertStmt.setString(i++, ingredient.getCategory().toString());
            }
            ingredientInsertStmt.executeUpdate();
            connection.commit();

            StringBuilder sql = new StringBuilder(
                    """
                            SELECT i.id as ingId, i.name as ingName, i.price, i.category,
                            d.id as dishId, d.name as dishName, dish_type as dishType
                            FROM ingredient AS i
                            JOIN dishingredient ON i.id = id_ingredient
                            JOIN dish AS d ON d.id = id_dish
                            WHERE 1 = 1
                            """
            );

            for (int j = 0; j < newIngredients.size(); j++) {
                if (j == 0) {
                    sql.append(" AND i.id = ?");
                } else sql.append(" OR i.id = ?");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
            i = 1;
            for (Ingredient ingredient : newIngredients) {
                preparedStatement.setInt(i++, ingredient.getId());
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Ingredient> ingredients = new ArrayList<>();
            createIngredientList(resultSet, ingredients);
            return ingredients;
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public Dish saveDish(Dish dishToSave) throws SQLException {
        Connection connection = dbConnection.getConnection();
        try {
            connection.setAutoCommit(false);

            String linkDelete = "DELETE FROM dishingredient WHERE id_dish = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(linkDelete);
            preparedStatement.setInt(1, dishToSave.getId());
            preparedStatement.executeUpdate();

            if (!dishToSave.getIngredients().isEmpty()) {
                PreparedStatement ingredientInsertStmt = connection.prepareStatement(
                        "INSERT INTO ingredient(id, name, price, category) VALUES " +
                                "(?, ?, ?, CAST(? AS category_enum)), ".repeat(dishToSave.getIngredients().size() - 1) +
                                "(?, ?, ?, CAST(? AS category_enum))"
                );

                int i = 1;
                for (Ingredient ingredient : dishToSave.getIngredients()) {
                    ingredientInsertStmt.setInt(i++, ingredient.getId());
                    ingredientInsertStmt.setString(i++, ingredient.getName());
                    ingredientInsertStmt.setDouble(i++, ingredient.getPrice());
                    ingredientInsertStmt.setString(i++, ingredient.getCategory().toString());
                }
                ingredientInsertStmt.executeUpdate();
            }

            PreparedStatement dishUpdateStatement = connection.prepareStatement(
                    """
                            INSERT INTO dish (id, name, dish_type, price) VALUES
                            (?, ?, ?::dish_type_enum, ?)
                            ON CONFLICT (id) DO UPDATE
                            SET name = EXCLUDED.name, dish_type = EXCLUDED.dish_type,price = EXCLUDED.price
                            RETURNING id
                            """);
            dishUpdateStatement.setInt(1, dishToSave.getId());
            dishUpdateStatement.setString(2, dishToSave.getName());
            dishUpdateStatement.setString(3, dishToSave.getDishType().toString());
            if (dishToSave.getPrice() != null) {
                dishUpdateStatement.setDouble(4, dishToSave.getPrice());
            } else {
                dishUpdateStatement.setNull(4, Types.DOUBLE);
            }
            dishUpdateStatement.executeQuery();

            attachIngredient(connection, dishToSave);

            connection.commit();
            return findByDishId(dishToSave.getId());
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public List<Dish> findDishsByIngredientName(String ingredientName) throws SQLException {
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            SELECT d.id AS dishId, d.name AS dishName, d.dish_type AS dishType
                            FROM dish AS d
                            JOIN dishingredient ON d.id = id_dish
                            JOIN ingredient ON ingredient.id = id_ingredient
                            WHERE ingredient.name ILIKE ?
                            """);
            preparedStatement.setString(1, '%' + ingredientName + '%');
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Dish> dishes = new ArrayList<>();
            while (resultSet.next()) {
                /* if (resultSet.isFirst()) {
                    int dishId = resultSet.getInt("dishId");
                    String dishName = resultSet.getString("dishName");
                    DishTypeEnum dishType = DishTypeEnum.valueOf(resultSet.getString("dishType"));
                    dishes.add(new Dish(dishId, dishName, dishType, new ArrayList<>()));
                } */
                dishes.add(findByDishId(resultSet.getInt("dishId")));
            }
            return dishes;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public List<Ingredient> findIngredientsByCriteria(String ingredientName,
                                                      CategoryEnum category,
                                                      String dishName,
                                                      int page,
                                                      int size) throws SQLException {
        Connection connection = dbConnection.getConnection();
        try {
            StringBuilder query = new StringBuilder(
                    """
                            SELECT i.id AS ingId, i.name AS ingName, i.price AS price, i.category AS category,
                            id_dish AS dishId, d.name AS dishName, dish_type AS dishType
                            FROM dish AS d
                            JOIN dishingredient ON d.id = id_dish
                            JOIN ingredient AS i ON i.id = id_ingredient
                            """);
            List<String> conditions = new ArrayList<>();
            if (ingredientName != null || category != null || dishName != null) {
                query.append(" WHERE ");
                if (ingredientName != null) {
                    conditions.add(" i.name ILIKE ? ");
                }
                if (category != null) {
                    conditions.add(" i.category = CAST(? AS category_enum) ");
                }
                if (dishName != null) {
                    conditions.add(" d.name ILIKE ? ");
                }
                query.append(conditions.stream().reduce((a, b) -> a + " AND " + b).get());
            }
            query.append(" LIMIT ? OFFSET ?");
            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            int i = 1;
            if (ingredientName != null) preparedStatement.setString(i++, '%' + ingredientName + '%');
            if (category != null) preparedStatement.setString(i++, category.toString());
            if (dishName != null) preparedStatement.setString(i++, '%' + dishName + '%');
            preparedStatement.setInt(i++, (size > 0 ? size : 10));
            preparedStatement.setInt(i++, (page > 0) ? ((page - 1) * size) : 1);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Ingredient> ingredients = new ArrayList<>();
            createIngredientList(resultSet, ingredients);
            return ingredients;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public void attachIngredient(Connection connection, Dish dish) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                            INSERT INTO dishingredient (id, id_dish, id_ingredient, quantity_required, unit) VALUES
                            (?, ?, ?, ?, ?::unit_type)
                    """);

            for (Ingredient ingredient : dish.getIngredients()) {
                preparedStatement.setInt(1, getNextSequenceValue(connection,
                        "dishingredient",
                        "dishingredient_id_seq"));
                preparedStatement.setInt(2, dish.getId());
                preparedStatement.setInt(3, ingredient.getId());
                if (ingredient.getQuantity() == null || ingredient.getUnit() == null) {
                    throw new SQLException("Ingredient quantity or unit is null");
                }
                preparedStatement.setDouble(4, ingredient.getQuantity());
                preparedStatement.setString(5, ingredient.getUnit().toString());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }

    // Helper Functions
    private int getNextSequenceValue(Connection connection, String tableName, String sequenceName) throws SQLException {
        try {
            connection.createStatement().execute("SELECT setval('%s', (SELECT MAX(id) FROM %s))".formatted(sequenceName, tableName));
            ResultSet rs = connection.createStatement().executeQuery("SELECT nextval('%s')".formatted(sequenceName));
            rs.next();
            return rs.getInt(1);
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }

    private void createIngredientList(ResultSet resultSet, List<Ingredient> ingredients) throws SQLException {
        while (resultSet.next()) {

            Dish dish = null;
            if (resultSet.getObject("dishId") != null) {
                dish = new Dish(
                        resultSet.getInt("dishId"),
                        resultSet.getString("dishName"),
                        DishTypeEnum.valueOf(resultSet.getString("dishType")),
                        new ArrayList<>()
                );
            }

            ingredients.add(new Ingredient(
                    resultSet.getInt("ingId"),
                    resultSet.getString("ingName"),
                    resultSet.getDouble("price"),
                    CategoryEnum.valueOf(resultSet.getString("category")),
                    dish
            ));
        }
    }
}
