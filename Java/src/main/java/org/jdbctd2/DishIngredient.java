package org.jdbctd2;

import java.util.Objects;

// This class stills needs to be implemented into Dish
public class DishIngredient {
    int id;
    Dish dish;
    Ingredient ingredient;
    Double quantityRequired;
    UnitTypeEnum unit;

    public DishIngredient(int id, Dish dish, Ingredient ingredient, Double quantityRequired, UnitTypeEnum unit) {
        this.id = id;
        setDish(dish);
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(Double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public UnitTypeEnum getUnit() {
        return unit;
    }

    public void setUnit(UnitTypeEnum unit) {
        this.unit = unit;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
        this.dish.getIngredientsLinkList().add(this);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "id=" + id +
                ", dish=" + dish.getName() +
                ", ingredient=" + ingredient +
                ", quantityRequired=" + quantityRequired +
                ", unit=" + unit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DishIngredient that)) return false;
        return id == that.id && Double.compare(quantityRequired, that.quantityRequired) == 0 && Objects.equals(dish, that.dish) && Objects.equals(ingredient, that.ingredient) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dish, ingredient, quantityRequired, unit);
    }
}
