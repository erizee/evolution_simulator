package org.evo;

import java.util.Random;

public class PortalMap extends AbstractWorldMap {
    public PortalMap(int width, int height){
        super(width, height);
    }



    @Override
    public void moveAnimal(Animal animal) {
        Vector2d newPosition = animal.getOrientation().toUnitVector().add(animal.getPosition());
        Vector2d oldPosition = animal.getPosition();
        if (newPosition.y>height || newPosition.y<0 || newPosition.x>width || newPosition.x<0) {
                animal.energy-=animal.toBirthEnergy;
                newPosition = new Vector2d(getRandomNumber(0, width), getRandomNumber(0, height));
        }
        animal.positionChanged(animal, oldPosition, newPosition);
        animal.setPosition(newPosition);
    }

    public int getRandomNumber(int min, int max) {
        return new Random().nextInt(max+1) + min;
    }



}
