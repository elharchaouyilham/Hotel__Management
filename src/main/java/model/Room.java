package model;

import model.enums.RoomStatus;
import model.enums.RoomType;
import java.math.BigDecimal;
import java.util.UUID;

public class Room {

    private UUID id;
    private int number;
    private RoomType type;
    private BigDecimal price;
    private RoomStatus status;

    public Room() {
    }

    public Room(UUID id, int number, RoomType type,
                BigDecimal price, RoomStatus status) {

        this.id = id;
        this.number = number;
        this.type = type;
        this.price = price;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}