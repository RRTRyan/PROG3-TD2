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
                            SELECT dish.id AS dish_id, dish.name AS dish_name, dish_type,
                                    ingredient.id AS ingredient_id, ingredient.name AS ingredient_name,
                                    ingredient.price AS price, ingredient.category AS category
                            FROM dish LEFT JOIN ingredient ON id_dish = dish.id WHERE dish.id = ?
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Dish dish = null;
            while (resultSet.next()) {
                if (resultSet.isFirst()) {
                    int dishId = resultSet.getInt("dish_id");
                    String name = resultSet.getString("dish_name");
                    DishTypeEnum dishType = DishTypeEnum.valueOf(resultSet.getString("dish_type"));
                    dish = new Dish(dishId, name, dishType, new ArrayList<>());
                }

                if (resultSet.getObject("ingredient_id") != null) {
                    int ingredientId = resultSet.getInt("ingredient_id");
                    String ingredientName = resultSet.getString("ingredient_name");
                    double price = resultSet.getDouble("price");
                    CategoryEnum category = CategoryEnum.valueOf(resultSet.getString("category"));

                    if (dish != null) new Ingredient(ingredientId, ingredientName, price, category, dish);
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
                            SELECT i.id as ingredientId, i.name as ingredientName, i.price, i.category
                                 , d.id as dishId, d.name as dishName, dish_type as dishType
                            FROM ingredient as i LEFT JOIN dish as d ON d.id = i.id_dish
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

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO ingredient VALUES " + "(?, ?, ?, CAST(? AS category_enum), ?), ".repeat(newIngredients.size() - 1) +
                            "(?, ?, ?, CAST(? AS category_enum), ?)"
            );
            int i = 1;
            for (Ingredient ingredient : newIngredients) {
                preparedStatement.setInt(i++, ingredient.getId());
                preparedStatement.setString(i++, ingredient.getName());
                preparedStatement.setDouble(i++, ingredient.getPrice());
                preparedStatement.setString(i++, ingredient.getCategory().toString());
                if (ingredient.getDish() != null) {
                    preparedStatement.setInt(i++, ingredient.getDish().getId());
                } else preparedStatement.setNull(i++, Types.INTEGER);
            }
            preparedStatement.executeUpdate();

            StringBuilder sql = new StringBuilder(
                    "SELECT i.id as ingredientId, i.name as ingredientName, i.price, i.category " +
                            ", d.id as dishId, d.name as dishName, dish_type as dishType " +
                            "FROM ingredient as i LEFT JOIN dish as d ON d.id = i.id_dish " +
                            "WHERE 1 = 1"
            );

            for (int j = 0; j < newIngredients.size(); j++) {
                if (j == 0) {
                    sql.append(" AND i.id = ?");
                } else sql.append(" OR i.id = ?");
            }

            preparedStatement = connection.prepareStatement(sql.toString());
            i = 1;
            for (Ingredient ingredient : newIngredients) {
                preparedStatement.setInt(i++, ingredient.getId());
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Ingredient> ingredients = new ArrayList<>();
            createIngredientList(resultSet, ingredients);
            connection.commit();
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
            Dish initialDish = findByDishId(dishToSave.getId());

            if (initialDish != null) {
                List<Ingredient> removedIngredients = initialDish.getIngredients().stream()
                        .filter(ingredient -> !dishToSave.getIngredients().contains(ingredient))
                        .toList();

                List<Ingredient> newIngredients = dishToSave.getIngredients().stream()
                        .filter(ingredient -> ((!removedIngredients.contains(ingredient)) && (!initialDish.getIngredients().contains(ingredient))))
                        .toList();

                if (!removedIngredients.isEmpty()) {
                    StringBuilder sql = new StringBuilder("DELETE FROM ingredient WHERE id_dish = ?");
                    for (int i = 0; i < removedIngredients.size(); i++) {
                        if (i == 0) {
                            sql.append(" AND (id = ?");
                        } else {
                            sql.append(" OR id = ?");
                        }
                        if (i == removedIngredients.size() - 1) {
                            sql.append(")");
                        }
                    }
                    PreparedStatement ingredientsRemovalStatement = connection.prepareStatement(sql.toString());
                    ingredientsRemovalStatement.setInt(1, dishToSave.getId());
                    int i = 2;
                    for (Ingredient ingredient : removedIngredients) {
                        ingredientsRemovalStatement.setInt(i++, ingredient.getId());
                    }
                    ingredientsRemovalStatement.executeUpdate();
                    connection.commit();
                }
                createIngredients(newIngredients);

                PreparedStatement dishUpdateStatement = connection.prepareStatement("UPDATE dish SET dish_type = CAST(? AS dish_type_enum), name = ? WHERE id = ?");
                dishUpdateStatement.setString(1, dishToSave.getDishType().toString());
                dishUpdateStatement.setString(2, dishToSave.getName());
                dishUpdateStatement.setInt(3, dishToSave.getId());
                dishUpdateStatement.executeUpdate();
                connection.commit();

            } else {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO dish VALUES (?, ?, CAST(? AS dish_type_enum))");
                preparedStatement.setInt(1, dishToSave.getId());
                preparedStatement.setString(2, dishToSave.getName());
                preparedStatement.setString(3, dishToSave.getDishType().toString());
                preparedStatement.executeUpdate();
                connection.commit();
                createIngredients(dishToSave.getIngredients());
            }
            connection.commit();
            return findByDishId(dishToSave.getId());
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public List<Dish> findDishByIngredientsName(String ingredientName) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Ingredient> findIngredientsByCriteria(String ingredientName,
                                                      CategoryEnum category,
                                                      String dishName,
                                                      int page,
                                                      int size) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Helper Functions
    private void updateTableSequence(String tableName, String sequenceName) throws SQLException {
        Connection connection = dbConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SETVAL(?, (SELECT MAX(id) FROM ?) + 1) ");
        preparedStatement.setString(1, sequenceName);
        preparedStatement.setString(2, tableName);
        preparedStatement.executeUpdate();
    }

    private void createIngredientList(ResultSet resultSet, List<Ingredient> ingredients) throws SQLException {
        while (resultSet.next()) {

            Dish dish = null;
            if (resultSet.getObject("dishId") != null) new Dish(
                    resultSet.getInt("dishId"),
                    resultSet.getString("dishName"),
                    DishTypeEnum.valueOf(resultSet.getString("dishType")),
                    new ArrayList<>()
            );

            ingredients.add(new Ingredient(
                    resultSet.getInt("ingredientId"),
                    resultSet.getString("ingredientName"),
                    resultSet.getDouble("price"),
                    CategoryEnum.valueOf(resultSet.getString("category")),
                    dish
            ));
        }
    }
}
