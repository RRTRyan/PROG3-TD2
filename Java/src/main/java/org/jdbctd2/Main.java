package org.jdbctd2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws SQLException {
        DataRetriever dataRetriever = new DataRetriever(new DBConnection());

        System.out.println("------ Find dish by ID");
        System.out.println(dataRetriever.findByDishId(1));
        // System.out.println(dataRetriever.findByDishId(999));

        System.out.println("\n");
        System.out.println("------ Find ingredients with pagination");
        System.out.println(dataRetriever.findIngredients(2, 2));
        System.out.println(dataRetriever.findIngredients(3, 5));

        System.out.println("\n");
        System.out.println("------ Create ingredients and return new ones");
        System.out.println(dataRetriever.createIngredients(Arrays.asList(new Ingredient(100, "Fromage", 1200, CategoryEnum.DAIRY, null), new Ingredient(101, "Oignon", 500, CategoryEnum.VEGETABLE, null))));
        // System.out.println(dataRetriever.createIngredients(Arrays.asList(new Ingredient(102, "Carotte", 2000, CategoryEnum.VEGETABLE, null), new Ingredient(103, "Laitue", 2000, CategoryEnum.VEGETABLE, null))));

    }
}
