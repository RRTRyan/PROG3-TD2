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

        // System.out.println("------ Find dish by ID");
        // System.out.println(dataRetriever.findByDishId(1));
        // System.out.println(dataRetriever.findByDishId(999));

        // System.out.println("\n");
        // System.out.println("------ Find ingredients with pagination");
        // System.out.println(dataRetriever.findIngredients(2, 2));
        // System.out.println(dataRetriever.findIngredients(3, 5));

        // System.out.println("\n");
        // System.out.println("------ Find dish by ingredient name");
        // System.out.println(dataRetriever.findDishsByIngredientName("eur"));

        // System.out.println("\n");
        // System.out.println("------ Find ingredients by criteria with pagination");
        // System.out.println(dataRetriever.findIngredientsByCriteria(null, CategoryEnum.VEGETABLE, null, 1, 10));
        // System.out.println(dataRetriever.findIngredientsByCriteria("cho", null, "Sal", 1, 10));
        // System.out.println(dataRetriever.findIngredientsByCriteria("cho", null, "gâteau", 1, 10));

        // System.out.println("\n");
        // System.out.println("------ Create ingredients and return new ones");
        // System.out.println(dataRetriever.createIngredients(Arrays.asList(new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY, null), new Ingredient(6, "Oignon", 500, CategoryEnum.VEGETABLE, null))));
        // System.out.println(dataRetriever.createIngredients(Arrays.asList(new Ingredient(8, "Carotte", 2000, CategoryEnum.VEGETABLE, null), new Ingredient(1, "Laitue", 2000, CategoryEnum.VEGETABLE, null))));

        // System.out.println("\n");
        // System.out.println("------ Saving or updating dishes");
        // Dish soupeDeLegumes = new Dish(6, "Soupe de légumes", DishTypeEnum.START, new ArrayList<>());
        // new Ingredient(6, "Oignon", 500, CategoryEnum.VEGETABLE, soupeDeLegumes);
        // System.out.println(dataRetriever.saveDish(soupeDeLegumes));

        // Dish saladeFraiche = new Dish(1, "Salade fraîche", DishTypeEnum.START, new ArrayList<>());
        // new Ingredient(6, "Oignon", 500, CategoryEnum.VEGETABLE,  saladeFraiche);
        // new Ingredient(1, "Laitue", 2000, CategoryEnum.VEGETABLE,  saladeFraiche);
        // new Ingredient(2, "Tomate", 600, CategoryEnum.VEGETABLE,  saladeFraiche);
        // new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY,  saladeFraiche);
        // System.out.println(dataRetriever.saveDish(saladeFraiche));

        // Dish saladeFraiche = new Dish(1, "Salade de fromage", DishTypeEnum.START, new ArrayList<>());
        // new Ingredient(7, "Fromage", 1200, CategoryEnum.DAIRY,  saladeFraiche);
        // System.out.println(dataRetriever.saveDish(saladeFraiche));
    }
}
