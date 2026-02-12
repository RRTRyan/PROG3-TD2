package org.jdbctd2;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection;

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
                                    di.id AS dishIngredientId, quantity_required, unit
                            FROM dish AS d
                            LEFT JOIN dishingredient AS di ON id_dish = d.id
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

                if (resultSet.getObject("ingId") != null && dish != null) {
                    int ingredientId = resultSet.getInt("ingId");
                    String ingredientName = resultSet.getString("ingName");
                    double price = resultSet.getDouble("price");
                    CategoryEnum category = CategoryEnum.valueOf(resultSet.getString("category"));

                    Ingredient newIngredient = new Ingredient(ingredientId, ingredientName, price, category);

                    int dishIngredientId = resultSet.getInt("dishIngredientId");
                    Double quantity = resultSet.getDouble("quantity_required");
                    UnitTypeEnum unitType = UnitTypeEnum.valueOf(resultSet.getString("unit"));
                    DishIngredient newDishIngredient = new DishIngredient(dishIngredientId, dish, newIngredient, quantity, unitType);
                }
            }
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
                            "(?, ?, ?, CAST(? AS category_enum)) ON CONFLICT (id) DO NOTHING"
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
                            LEFT JOIN dishingredient ON i.id = id_ingredient
                            LEFT JOIN dish AS d ON d.id = id_dish
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

            if (!dishToSave.getIngredientsLinkList().isEmpty()) {
                PreparedStatement ingredientInsertStmt = connection.prepareStatement(
                        "INSERT INTO ingredient(id, name, price, category) VALUES " +
                                "(?, ?, ?, CAST(? AS category_enum)), ".repeat(dishToSave.getIngredientsLinkList().size() - 1) +
                                "(?, ?, ?, CAST(? AS category_enum)) ON CONFLICT (id) DO NOTHING"
                );

                int i = 1;
                for (DishIngredient ingredientLink : dishToSave.getIngredientsLinkList()) {
                    ingredientInsertStmt.setInt(i++, ingredientLink.getIngredient().getId());
                    ingredientInsertStmt.setString(i++, ingredientLink.getIngredient().getName());
                    ingredientInsertStmt.setDouble(i++, ingredientLink.getIngredient().getPrice());
                    ingredientInsertStmt.setString(i++, ingredientLink.getIngredient().getCategory().toString());
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
                            RIGHT JOIN ingredient AS i ON i.id = id_ingredient
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
                            (?, ?, ?, ?, ?::unit_type) ON CONFLICT (id) DO NOTHING
                    """);

            for (DishIngredient ingredientLink : dish.getIngredientsLinkList()) {
                preparedStatement.setInt(1, getNextSequenceValue(connection,
                        "dishingredient",
                        "dishingredient_id_seq"));
                preparedStatement.setInt(2, dish.getId());
                preparedStatement.setInt(3, ingredientLink.getIngredient().getId());
                if (ingredientLink.getQuantityRequired() == null || ingredientLink.getUnit() == null) {
                    throw new SQLException("Ingredient quantity or unit is null");
                }
                preparedStatement.setDouble(4, ingredientLink.getQuantityRequired());
                preparedStatement.setString(5, ingredientLink.getUnit().toString());
                preparedStatement.execute();
            }
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }

    public Ingredient saveIngredient(Ingredient toSave) throws SQLException {
        Connection connection = dbConnection.getConnection();
        try {
            connection.setAutoCommit(false);
            createIngredients(List.of(toSave));
            createStockMovementRecord(connection, toSave);
            connection.commit();
            Ingredient ingredient = findIngredientsByCriteria(toSave.getName(), toSave.getCategory(), null, 1, 1).getLast();
            ingredient.setStockMovementList(getIngredientStockMovementList(connection, toSave.getId()));
            return ingredient;
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }

    }

    public Order saveOrder(Order orderToSave) throws SQLException {
        Connection connection = dbConnection.getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement psOrder = connection.prepareStatement("INSERT INTO \"order\"(id, reference, creation_datetime) VALUES (?, ?, ?) ON CONFLICT (id) DO NOTHING");
            psOrder.setInt(1, orderToSave.getId());
            psOrder.setString(2, orderToSave.getReference());
            psOrder.setTimestamp(3, Timestamp.from(orderToSave.getCreationDateTime()));
            psOrder.executeUpdate();

            PreparedStatement psDishOrder = connection.prepareStatement("INSERT INTO dishorder(id, id_order, id_dish, quantity) VALUES (?, ?, ?, ?)");
            for (DishOrder dishOrder : orderToSave.getDishOrders()) {
                findByDishId(dishOrder.getDish().getId()).getIngredientsLinkList().forEach(link -> {
                    Ingredient ing = link.getIngredient();
                    try {
                        ing.setStockMovementList(getIngredientStockMovementList(connection, ing.getId()));
                        if (ing.getStockMovementList().size() == 1) {
                            throw new RuntimeException("Ingredient has never been supplied");
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    if (UnitConversion.convertToKG(
                            ing.getStockValueAt(orderToSave.getCreationDateTime()),
                            ing.getName()).getQuantity()
                            < (link.getQuantityRequired() * dishOrder.getQuantity())) {
                        throw new RuntimeException("Unsufficient Ingredient Stock: [%s: Remaining=%f, Needed=%f]"
                                .formatted(ing.getName(),
                                        UnitConversion.convertToKG(ing.getStockValueAt(orderToSave.getCreationDateTime()), ing.getName()).getQuantity(),
                                        link.getQuantityRequired() * dishOrder.getQuantity()));
                }
                });
                psDishOrder.setInt(1, dishOrder.getId());
                psDishOrder.setInt(2, orderToSave.getId());
                psDishOrder.setInt(3, dishOrder.getDish().getId());
                psDishOrder.setInt(4, dishOrder.getQuantity());
                psDishOrder.addBatch();
            }
            psDishOrder.executeBatch();

            connection.commit();
            return findOrderByReference(orderToSave.getReference());
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public Order findOrderByReference(String reference) throws SQLException {
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement psOrder = connection.prepareStatement(
                    "SELECT o.id AS orderId, o.reference, o.creation_datetime, dio.id AS dishOrderId, dio.id_dish, dio.quantity " +
                    "FROM \"order\" AS o JOIN dishOrder AS dio ON o.id = dio.id_order " +
                    "WHERE reference ILIKE ?");
            psOrder.setString(1, reference);
            ResultSet rs = psOrder.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("orderId");
                String dbReference = rs.getString("reference");
                Instant creationDateTime = rs.getTimestamp("creation_datetime").toInstant();
                Order order = new Order(id, dbReference, creationDateTime);
                do {
                    order.getDishOrders().add(new DishOrder(rs.getInt("dishOrderId"), this.findByDishId(rs.getInt("id_dish")), rs.getInt("quantity")));
                } while (rs.next());
                return order;
            }
            throw new SQLException("Order not found");
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    // Still does not consider unit
    public StockValue getStockValueAtDB(Instant t, Integer ingId) {
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("""
                    SELECT SUM(
                    CASE "type"
                    WHEN 'IN'::stock_movement_enum THEN quantity
                    ELSE quantity * -1
                    END
                    ) AS actual_quantity, unit FROM stock_movement WHERE creation_datetime <= ? AND id_ingredient = ? GROUP BY (id_ingredient, unit) ORDER BY id_ingredient;
                    """);
            ps.setTimestamp(1, Timestamp.from(t));
            ps.setInt(2, ingId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new StockValue(rs.getDouble(1), UnitTypeEnum.valueOf(rs.getString(2)));
            }
            throw new SQLException("Stock Movement not found");
        } catch (SQLException | RuntimeException e) {
            dbConnection.closeConnection(connection);
            throw new RuntimeException(e);
        }
    }

    public Double getDishCost(Integer dishId) {
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT SUM(i.price * di.quantity_required) FROM ingredient AS i JOIN dishingredient AS di ON di.id_ingredient = i.id WHERE di.id_dish = ?");
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException | RuntimeException e) {
            dbConnection.closeConnection(connection);
            throw new RuntimeException(e);
        }
        return null;
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
            Ingredient ingredient = new Ingredient(
                    resultSet.getInt("ingId"),
                    resultSet.getString("ingName"),
                    resultSet.getDouble("price"),
                    CategoryEnum.valueOf(resultSet.getString("category"))
            );
            ingredient.setStockMovementList(getIngredientStockMovementList(dbConnection.getConnection(), ingredient.getId()));
            ingredients.add(ingredient);
        }
    }

    private void createStockMovementRecord(Connection connection, Ingredient ingredient) throws SQLException {
        try {
            PreparedStatement ps = connection.prepareStatement(("INSERT INTO stock_movement (id, id_ingredient, quantity, unit, type, creation_datetime) VALUES " +
                    "(?, ?, ?, ?::unit_type, ?::stock_movement_type, ?), ".repeat(Math.max(((ingredient.getStockMovementList().size()) - 1), 0)) +
                    "(?, ?, ?, ?::unit_type, ?::stock_movement_type, ?) ON CONFLICT (id) DO NOTHING"));
            int i = 1;
            for (StockMovement stockMovement : ingredient.getStockMovementList()) {
                ps.setInt(i++, stockMovement.getId());
                ps.setInt(i++, ingredient.getId());
                ps.setDouble(i++, stockMovement.getValue().getQuantity());
                ps.setString(i++, stockMovement.getValue().getUnit().toString());
                ps.setString(i++, stockMovement.getType().toString());
                ps.setTimestamp(i++, Timestamp.from(stockMovement.getCreationDateTime()));
            }
            ps.executeBatch();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }

    private List<StockMovement> getIngredientStockMovementList(Connection connection, Integer ingredientId) throws SQLException {
        if (connection == null) {
            connection = dbConnection.getConnection();
        }
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT id, quantity, unit, type, creation_datetime FROM stock_movement WHERE id_ingredient = ?");
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();
            List<StockMovement> stockMovementList = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                double quantity = rs.getDouble("quantity");
                UnitTypeEnum unit = UnitTypeEnum.valueOf(rs.getString("unit"));
                MovementTypeEnum movementType = MovementTypeEnum.valueOf(rs.getString("type"));
                Instant creationDateTime = rs.getTimestamp("creation_datetime").toInstant();
                stockMovementList.add(new StockMovement(id, new StockValue(quantity, unit), movementType, creationDateTime));
            }
            return stockMovementList;
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
