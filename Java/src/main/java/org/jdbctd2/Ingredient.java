package org.jdbctd2;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Ingredient {
    private int id;
    private String name;
    private double price;
    private CategoryEnum category;
    private Dish dish;
    private Double quantity;
    private UnitTypeEnum unit;
    private List<StockMovement> stockMovementList;

    public Ingredient(int id, String name, double price, CategoryEnum category, Dish dish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
        if (this.dish != null)
            this.dish.getIngredients().add(this);
        this.quantity = null;
        this.unit = null;
        this.stockMovementList = List.of();
    }

    public Ingredient(int id, String name, double price, CategoryEnum category, Dish dish, Double quantity,
                      UnitTypeEnum unit) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
        if (this.dish != null)
            this.dish.getIngredients().add(this);
        this.quantity = quantity;
        this.unit = unit;
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

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
        if (this.dish != null)
            this.dish.getIngredients().add(this);
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public UnitTypeEnum getUnit() {
        return unit;
    }

    public void setUnit(UnitTypeEnum unit) {
        this.unit = unit;
    }

    public List<StockMovement> getStockMovementList() {
        return stockMovementList;
    }

    public void setStockMovementList(List<StockMovement> stockMovements) {
        this.stockMovementList = stockMovements;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", dish=" + getDishName() +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", remaining stock=" + stockMovementList.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ingredient that))
            return false;
        return id == that.id && Double.compare(price, that.price) == 0 && Objects.equals(name, that.name)
                && category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category, dish);
    }

    public String getDishName() {
        return (this.dish != null) ? this.dish.getName() : null;
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
