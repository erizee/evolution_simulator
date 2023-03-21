package org.evo;

import java.util.Random;

public enum MapDirection {
    NORTH,
    SOUTH,
    WEST,
    EAST,
    NORTHWEST,
    NORTHEAST,
    SOUTHWEST,
    SOUTHEAST;


    final int MAP_DIR_LENGTH = 8;

    public MapDirection opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
            case NORTHWEST -> SOUTHEAST;
            case NORTHEAST -> SOUTHWEST;
            case SOUTHWEST -> NORTHEAST;
            case SOUTHEAST -> NORTHWEST;
        };
    }
    
    public MapDirection rotate(int rotation){
        int val = this.toInt();
        return MapDirection.values()[(val + rotation) % this.MAP_DIR_LENGTH];
    }


    public int toInt(){
        return switch (this) {
            case NORTH -> 0;
            case NORTHEAST -> 1;
            case EAST -> 2;
            case SOUTHEAST -> 3;
            case SOUTH -> 4;
            case SOUTHWEST -> 5;
            case WEST -> 6;
            case NORTHWEST -> 7;
        };
    }

    public Vector2d toUnitVector() {
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case SOUTH -> new Vector2d(0, -1);
            case WEST -> new Vector2d(-1, 0);
            case EAST -> new Vector2d(1, 0);
            case NORTHWEST -> new Vector2d(-1, 1);
            case NORTHEAST -> new Vector2d(1, 1);
            case SOUTHWEST -> new Vector2d(-1, -1);
            case SOUTHEAST -> new Vector2d(1, -1);
        };
    }
    public static MapDirection getRandomDirection(){
        return values()[new Random().nextInt(values().length)];
    }
}
