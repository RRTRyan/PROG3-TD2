package org.jdbctd2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws SQLException {
        DataRetriever dataRetriever = new DataRetriever(new DBConnection());

/*
        System.out.println("------ Find dish by ID");
        System.out.println(dataRetriever.findByDishId(1));
        try {
            System.out.println(dataRetriever.findByDishId(999));
        } catch (RuntimeException e) {
            System.out.println(e.toString());
        }
*/

/*
        System.out.println("\n");
        System.out.println("------ Find ingredients with pagination");
        System.out.println(dataRetriever.findIngredients(2, 2));
        System.out.println(dataRetriever.findIngredients(3, 5));
*/

/*
        System.out.println("\n");
        System.out.println("------ Find dish by ingredient name");
        System.out.println(dataRetriever.findDishsByIngredientName("eur"));
*/

/*
        System.out.println("\n");
        System.out.println("------ Find ingredients by criteria with pagination");
        System.out.println(dataRetriever.findIngredientsByCriteria(null, CategoryEnum.VEGETABLE, null, 1, 10));
        System.out.println(dataRetriever.findIngredientsByCriteria("cho", null, "Sal", 1, 10));
        System.out.println(dataRetriever.findIngredientsByCriteria("cho", null, "gâteau", 1, 10));
*/

/*
        System.out.println("\n");
        System.out.println("------ Create ingredients and return new ones");
        System.out.println(dataRetriever.createIngredients(Arrays.asList(new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY, null), new Ingredient(6, "Oignon", 500, CategoryEnum.VEGETABLE, null))));
*/

/*
        try {
            System.out.println(dataRetriever.createIngredients(Arrays.asList(new Ingredient(8, "Carotte", 2000, CategoryEnum.VEGETABLE, null), new Ingredient(1, "Laitue", 2000, CategoryEnum.VEGETABLE, null))));
        } catch (RuntimeException e) {
            System.out.println(e.toString());
        }
*/

/*
        System.out.println("\n");
        System.out.println("------ Saving or updating dishes");
        Dish soupeDeLegumes = new Dish(6, "Soupe de légumes", DishTypeEnum.START, new ArrayList<>());
        new Ingredient(6, "Oignon", 500, CategoryEnum.VEGETABLE, soupeDeLegumes, 3000.00, UnitTypeEnum.PCS);
        System.out.println(dataRetriever.saveDish(soupeDeLegumes));
*/

/*
        Dish saladeFraiche = new Dish(1, "Salade fraîche", DishTypeEnum.START, new ArrayList<>());
        new Ingredient(6, "Oignon", 500, CategoryEnum.VEGETABLE,  saladeFraiche);
        new Ingredient(1, "Laitue", 2000, CategoryEnum.VEGETABLE,  saladeFraiche);
        new Ingredient(2, "Tomate", 600, CategoryEnum.VEGETABLE,  saladeFraiche);
        new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY,  saladeFraiche);
        System.out.println(dataRetriever.saveDish(saladeFraiche));
*/

        // Dish saladeFraiche = new Dish(1, "Salade de fromage", DishTypeEnum.START, new ArrayList<>());
        // new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY,  saladeFraiche);
        // System.out.println(dataRetriever.saveDish(saladeFraiche));

/*
        System.out.println("\n");
        System.out.println("----- Get & update/save dish grossMargin");
        System.out.println(dataRetriever.findByDishId(1).getGrossMargin());
        try {
            System.out.println(dataRetriever.findByDishId(3).getGrossMargin());
        } catch (RuntimeException e) {
            System.out.println(e.toString());
        }
*/

/*
        Dish saladeFraiche = new Dish(1, "Salade de fromage", DishTypeEnum.START, new ArrayList<>());
        new Ingredient(6, "Fromage", 1200, CategoryEnum.DAIRY,  saladeFraiche, 0.5, UnitTypeEnum.KG);
        saladeFraiche.setPrice(2000.00);
        System.out.println(dataRetriever.saveDish(saladeFraiche));
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
            System.out.println(dataRetriever.findByDishId(3).getGrossMargin()); // 0
        } catch (RuntimeException e) {System.out.println(e.toString());}
        System.out.println(dataRetriever.findByDishId(4).getGrossMargin()); // 6600
        try {
            System.out.println(dataRetriever.findByDishId(5).getGrossMargin()); // 0
        } catch (RuntimeException e) {System.out.println(e.toString());}
*/
    }
}
