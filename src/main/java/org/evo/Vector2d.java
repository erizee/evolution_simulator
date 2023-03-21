package org.evo;

import java.util.Random;

public class Vector2d {
    public int x;
    public int y;
    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return String.format("(%d,%d)", x, y);
    }

    public boolean precedes(Vector2d other) {
        return (x<=other.x && y<=other.y);
    }

    public boolean follows(Vector2d other) {
        return (x>=other.x && y>=other.y);
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(x+other.x, y+other.y);
    }

    public Vector2d subtract(Vector2d other) {
        return new Vector2d(x-other.x, y-other.y);
    }

    public Vector2d upperRight(Vector2d other) {
        return new Vector2d(Math.max(x,other.x), Math.max(y,other.y));
    }

    public Vector2d lowerLeft(Vector2d other) {
        return new Vector2d(Math.min(x,other.x), Math.min(y,other.y));
    }

    public Vector2d opposite() {
        return new Vector2d(-x, -y);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        Vector2d that = (Vector2d) obj;
        return x == that.x && y==that.y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + x;
        hash = 47 * hash + y;
        return hash;
    }
}
