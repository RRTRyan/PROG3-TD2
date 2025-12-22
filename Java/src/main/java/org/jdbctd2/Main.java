package org.jdbctd2;

import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws SQLException {
        DataRetriever dataRetriever = new DataRetriever(new DBConnection());

        System.out.println(dataRetriever.findByDishId(1));
        // System.out.println(dataRetriever.findByDishId(999));

        System.out.println(dataRetriever.findIngredients(2, 2));
        System.out.println(dataRetriever.findIngredients(3, 5));
    }
}
