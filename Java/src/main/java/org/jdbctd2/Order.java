package org.jdbctd2;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private int id;
    private String reference;
    private Instant creationDateTime;
    private List<DishOrder> dishOrders;

    public Order(int id, String reference, Instant creationDateTime) {
        this.id = id;
        if (validateReference(reference)) {
            this.reference = reference;
        } else {
            throw new IllegalArgumentException("Invalid reference format");
        }
        this.creationDateTime = creationDateTime;
        this.dishOrders = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Order order)) return false;
        return id == order.id && Objects.equals(reference, order.reference) && Objects.equals(creationDateTime, order.creationDateTime) && Objects.equals(dishOrders, order.dishOrders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, creationDateTime, dishOrders);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", creationDateTime=" + creationDateTime +
                ", dishOrders=" + dishOrders +
                '}';
    }

    public Double getTotalAmountWithoutVAT() {
        return this.dishOrders.stream().mapToDouble((dish1) -> (dish1.getQuantity() * dish1.getDish().getPrice())).sum();
    }

    public Double getTotalAmountWithVAT() {
        return getTotalAmountWithoutVAT() * 1.1;
    }

    private boolean validateReference(String reference) {
        return !((reference == null) || (reference.length() != 8) || (!reference.startsWith("ORD")));
    }
}
