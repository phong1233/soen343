package org.team4.house.components;

public class Wall {
    public String type;
    public boolean open = false;
    public boolean blocked = false;

    public Wall(String type) {
        this.type = type;
    }
}
