package org.jdbctd2;

import java.time.Instant;

public class StockMovement {
    private int id;
    private StockValue value;
    private MovementTypeEnum type;
    private Instant creationDateTime;

    public StockMovement(int id, StockValue value, MovementTypeEnum movementType, Instant movementDate) {
        this.id = id;
        this.value = value;
        this.type = movementType;
        this.creationDateTime = movementDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StockValue getValue() {
        return value;
    }

    public void setValue(StockValue value) {
        this.value = value;
    }

    public MovementTypeEnum getType() {
        return type;
    }

    public void setType(MovementTypeEnum movementType) {
        this.type = movementType;
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Instant movementDate) {
        this.creationDateTime = movementDate;
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", value=" + value +
                ", type=" + type +
                ", creationDateTime=" + creationDateTime +
                '}';
    }
}
