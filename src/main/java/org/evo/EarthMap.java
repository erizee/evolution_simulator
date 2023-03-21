package org.evo;

import static java.lang.System.out;

public class EarthMap extends AbstractWorldMap{

    public EarthMap(int width, int height){
        super(width, height);
    }
    @Override
    public void moveAnimal(Animal animal) {
        Vector2d newPosition = new Vector2d(mod(animal.getPosition().x+ animal.getOrientation().toUnitVector().x,width+1),
                                            animal.getPosition().y+ animal.getOrientation().toUnitVector().y);
        Vector2d oldPosition = animal.getPosition();
        if (newPosition.y>height ||newPosition.y<0) {
            animal.setOrientation(animal.getOrientation().opposite());
        }
        else {
            animal.positionChanged(animal, oldPosition, newPosition);
            animal.setPosition(newPosition);
        }
    }

    public int mod(int n, int m) {
        return (((n % m) + m) % m);
    }
}
