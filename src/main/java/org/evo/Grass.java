package org.evo;

public class Grass implements IMapElement{
    private final Vector2d position;
    private int grassNutritionalValue;
    Grass(Vector2d position, int grassNutritionalValue) {
        this.position = position;
        this.grassNutritionalValue = grassNutritionalValue;
    }

    public int getGrassNutritionalValue() {
        return grassNutritionalValue;
    }

    public Vector2d getPosition() {
        return position;
    }


}
