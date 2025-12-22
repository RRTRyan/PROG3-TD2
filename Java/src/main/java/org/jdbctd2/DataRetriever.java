package org.jdbctd2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private DBConnection dbConnection;

    public DataRetriever(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Dish findByDishId(Integer id) throws SQLException {
        Connection connection = dbConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                   """
                   SELECT dish.name AS dish_name, dish_type,
                           ingredient.id AS ingredient_id, ingredient.name AS ingredient_name,
                           ingredient.price AS price, ingredient.category AS category
                   FROM dish LEFT JOIN ingredient ON id_dish = dish.id WHERE dish.id = ?
                   """);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        Dish dish = null;
        while (resultSet.next()) {
            if (resultSet.isFirst()) {
                String name = resultSet.getString("dish_name");
                DishTypeEnum dishType = DishTypeEnum.valueOf(resultSet.getString("dish_type"));
                dish = new Dish(id, name, dishType, new ArrayList<>());
            }

            int  ing_id = resultSet.getInt("ingredient_id");
            String name = resultSet.getString("ingredient_name");
            double  price = resultSet.getDouble("price");
            CategoryEnum category = CategoryEnum.valueOf(resultSet.getString("category"));

            if (dish != null) dish.getIngredients().add(new Ingredient(ing_id, name, price, category, dish));
        }
        preparedStatement.close();
        connection.close();
        if (dish == null) {
            throw new RuntimeException("Dish not found");
        }
        return dish;
    }

    public List<Ingredient> findIngredients(int page, int size) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Dish saveDish(Dish dishToSave) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Dish> findDishByIngredientsName(String ingredientName) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Ingredient> findIngredientsByCriteria(String ingredientName,
                                                      CategoryEnum category,
                                                      String dishName,
                                                      int page,
                                                      int size) throws  SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
