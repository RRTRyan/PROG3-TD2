package org.jdbctd2;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws SQLException {
        DataRetriever dataRetriever = new DataRetriever(new DBConnection());

/*
        System.out.println("------ Find dish by ID");
        System.out.println(dataRetriever.findByDishId(1)); // Salade fraîche
        try {
            System.out.println(dataRetriever.findByDishId(999));
        } catch (RuntimeException e) {
            System.out.println(e);
        }
*/

/*
        System.out.println("\n");
        System.out.println("------ Find ingredients with pagination");
        System.out.println(dataRetriever.findIngredients(2, 2)); // [Poulet, Chocolat]
        System.out.println(dataRetriever.findIngredients(3, 5)); // []
*/

/*
        System.out.println("\n");
        System.out.println("------ Find dish by ingredient name");
        System.out.println(dataRetriever.findDishsByIngredientName("eur")); // Gâteau au chocolat
*/

/*
        System.out.println("\n");
        System.out.println("------ Find ingredients by criteria with pagination");
        System.out.println(dataRetriever.findIngredientsByCriteria(null, CategoryEnum.VEGETABLE, null, 1, 10)); // [Laitue, Tomate]
        System.out.println(dataRetriever.findIngredientsByCriteria("cho", null, "Sal", 1, 10)); // []
        System.out.println(dataRetriever.findIngredientsByCriteria("cho", null, "gâteau", 1, 10)); // [Chocolat]
*/

/*
        System.out.println("\n");
        System.out.println("------ Create ingredients and return new ones");
        System.out.println(dataRetriever.createIngredients(Arrays.asList(new Ingredient(6, "Oignon", 500, CategoryEnum.VEGETABLE), new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY)))); // [Oignon, Fromage]

        try {
            System.out.println(dataRetriever.createIngredients(Arrays.asList(new Ingredient(8, "Carotte", 2000, CategoryEnum.VEGETABLE), new Ingredient(1, "Laitue", 2000, CategoryEnum.VEGETABLE)))); // [Laitue, Carotte]
        } catch (RuntimeException e) {
            System.out.println(e);
        }
*/

/*
        System.out.println("\n");
        System.out.println("------ Saving or updating dishes");
        try {
            Dish soupeDeLegumes = new Dish(6, "Soupe de légumes", DishTypeEnum.START, new ArrayList<>());
            Ingredient oignon = new Ingredient(6, "Oignon", 500, CategoryEnum.VEGETABLE);
            new DishIngredient(9, soupeDeLegumes, oignon, 1D, UnitTypeEnum.PCS);
            System.out.println(dataRetriever.saveDish(soupeDeLegumes));
        } catch (RuntimeException _) {}

        try {
            Dish saladeFraiche = new Dish(1, "Salade fraîche", DishTypeEnum.START, new ArrayList<>());
            Ingredient oignon = new Ingredient(6, "Oignon", 500, CategoryEnum.VEGETABLE);
            Ingredient laitue = new Ingredient(1, "Laitue", 2000, CategoryEnum.VEGETABLE);
            Ingredient tomate = new Ingredient(2, "Tomate", 600, CategoryEnum.VEGETABLE);
            Ingredient fromage = new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY);
            new DishIngredient(10, saladeFraiche, oignon, 1D, UnitTypeEnum.PCS);
            new DishIngredient(11, saladeFraiche, laitue, 2D, UnitTypeEnum.PCS);
            new DishIngredient(12, saladeFraiche, tomate, 1D, UnitTypeEnum.PCS);
            new DishIngredient(13, saladeFraiche, fromage, 0.1D, UnitTypeEnum.KG);
            System.out.println(dataRetriever.saveDish(saladeFraiche));
        } catch (RuntimeException _) {}

        try {
            Dish saladeFraiche = new Dish(1, "Salade fraîche", DishTypeEnum.START, new ArrayList<>());
            Ingredient fromage = new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY);
            new DishIngredient(13, saladeFraiche, fromage, 0.1D, UnitTypeEnum.KG);
            System.out.println(dataRetriever.saveDish(saladeFraiche));
        } catch (RuntimeException _) {}
*/

/*
        System.out.println("\n");
        System.out.println("----- Get & update/save dish grossMargin");
        System.out.println(dataRetriever.findByDishId(1).getGrossMargin());
        try {
            System.out.println(dataRetriever.findByDishId(3).getGrossMargin());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        try {
            Dish saladeFraiche = new Dish(1, "Salade fraîche", DishTypeEnum.START, new ArrayList<>());
            Ingredient fromage = new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY);
            new DishIngredient(13, saladeFraiche, fromage, 0.1D, UnitTypeEnum.KG);
            saladeFraiche.setPrice(2000.0D);
            System.out.println(dataRetriever.saveDish(saladeFraiche));
        } catch (RuntimeException _) {}
*/

/*
        System.out.println("\n");
        System.out.println("------ getDishCost()");
        System.out.println(dataRetriever.findByDishId(1).getDishCost()); // 250
        System.out.println(dataRetriever.findByDishId(2).getDishCost()); // 4500
        System.out.println(dataRetriever.findByDishId(3).getDishCost()); // 0
        System.out.println(dataRetriever.findByDishId(4).getDishCost()); // 1400
        System.out.println(dataRetriever.findByDishId(5).getDishCost()); // 0
*/

/*
        System.out.println("\n");
        System.out.println("------ getGrossMargin()");
        System.out.println(dataRetriever.findByDishId(1).getGrossMargin()); // 3250
        System.out.println(dataRetriever.findByDishId(2).getGrossMargin()); // 7500
        try {
            System.out.println(dataRetriever.findByDishId(3).getGrossMargin()); // Err
        } catch (RuntimeException e) {System.out.println(e.getMessage());}
        System.out.println(dataRetriever.findByDishId(4).getGrossMargin()); // 6600
        try {
            System.out.println(dataRetriever.findByDishId(5).getGrossMargin()); // Err
        } catch (RuntimeException e) {System.out.println(e.getMessage());}
*/

/*
        System.out.println("\n");
        System.out.println("------ getStockValueAt(Instant t)");
        dataRetriever.findIngredients(1, 5)
                .forEach(ingredient -> System.out.println(ingredient.getStockValueAt(Instant.parse("2024-01-06T18:00:00Z"))));
*/

/*
        try {
            System.out.println("\n");
            System.out.println("------ saveIngredient()");
            Ingredient fromage = new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY);
            System.out.println(dataRetriever.saveIngredient(fromage).getStockValueAt(Instant.parse("2024-01-06T12:00:00Z")));
        } catch (RuntimeException _) {}
*/

/*
        System.out.println("\n");
        System.out.println("------ saveOrder()");
        try {
            Order order = new Order(10, "ORD00002", Instant.now());
            Dish saladeFraiche = new Dish(1, "Salade fraîche", DishTypeEnum.START, new ArrayList<>());
            order.getDishOrders().add(new DishOrder(10, saladeFraiche, 2));
            System.out.println(dataRetriever.saveOrder(order));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
*/

        try {
            System.out.println("\n");
            System.out.println("------ findOrderByReference()");
            Order order = dataRetriever.findOrderByReference("ORD00002");
            System.out.println(order);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
