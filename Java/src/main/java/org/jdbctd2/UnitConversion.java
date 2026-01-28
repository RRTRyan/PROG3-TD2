package org.jdbctd2;

public class UnitConversion {
    public static StockValue convertToKG(StockValue stockValue, String ingredientName) {
        switch (stockValue.getUnit()) {
            case L:
                stockValue.setUnit(UnitTypeEnum.KG);
                switch (ingredientName) {
                    case "Tomate", "Laitue", "Poulet":
                        throw new RuntimeException("Cannot convert to litre");
                    case "Chocolat":
                        stockValue.setQuantity(stockValue.getQuantity() / 2.5);
                        break;
                    case "Beurre":
                        stockValue.setQuantity(stockValue.getQuantity() / 5.0);
                        break;
                }
                break;
            case PCS:
                stockValue.setUnit(UnitTypeEnum.KG);
                switch (ingredientName) {
                    case "Tomate", "Chocolat":
                        stockValue.setQuantity(stockValue.getQuantity() / 10.0);
                        break;
                    case "Laitue":
                        stockValue.setQuantity(stockValue.getQuantity() / 2.0);
                        break;
                    case "Poulet":
                        stockValue.setQuantity(stockValue.getQuantity() / 8.0);
                        break;
                    case "Beurre":
                        stockValue.setQuantity(stockValue.getQuantity() / 4.0);
                        break;

                }
                break;
            default:
                break;
        }
        return stockValue;
    }
}