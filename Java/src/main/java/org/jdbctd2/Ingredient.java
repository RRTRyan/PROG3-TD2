package org.jdbctd2;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Ingredient {
    private int id;
    private String name;
    private double price;
    private CategoryEnum category;
    private List<StockMovement> stockMovementList;

    public Ingredient(int id, String name, double price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stockMovementList = List.of();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public List<StockMovement> getStockMovementList() {
        return stockMovementList;
    }

    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ingredient that)) return false;
        return id == that.id && Double.compare(price, that.price) == 0 && Objects.equals(name, that.name) && category == that.category && Objects.equals(stockMovementList, that.stockMovementList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category, stockMovementList);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", stockMovementList=" + stockMovementList.toString() +
                '}';
    }

    public StockValue getStockValueAt(Instant t) {
        if (this.stockMovementList.isEmpty()) {
            throw new RuntimeException("Ingredient has no stock history");
        }
        double quantity = this.getStockMovementList().stream()
                .filter(stock -> stock.getCreationDateTime().isBefore(t))
                .mapToDouble(stockMovement -> {
                    return switch (stockMovement.getType()) {
                        case MovementTypeEnum.IN -> stockMovement.getValue().getQuantity();
                        case MovementTypeEnum.OUT -> -stockMovement.getValue().getQuantity();
                    };
                }).sum();
        return new StockValue(quantity, (this.stockMovementList.getFirst().getValue().unit != null) ? this.stockMovementList.getFirst().getValue().unit : null);

/*
        return this.getStockMovementList().stream()
                .filter(stock -> stock.getCreationDateTime().isBefore(t))
                .max(Comparator.comparing(StockMovement::getCreationDateTime))
                .orElse(new StockMovement(0, new StockValue(0, UnitTypeEnum.KG), MovementTypeEnum.OUT, Instant.now()))
                .getValue();
*/
    }

/*
    public double getStockAt(Instant t) {
        return this.getStockMovementList().stream()
                .filter(stock -> stock.getCreationDateTime().isBefore(t))
                .mapToDouble(stockMovement -> {
                    return switch (stockMovement.getType()) {
                        case MovementTypeEnum.IN -> stockMovement.getValue().getQuantity();
                        case MovementTypeEnum.OUT -> -stockMovement.getValue().getQuantity();
                    };
                }).sum();
    }
*/
}
