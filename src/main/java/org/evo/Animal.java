package org.evo;


import java.util.ArrayList;
import java.util.Random;


public class Animal implements IPositionChangeObserver, IMapElement{
    private MapDirection orientation = MapDirection.getRandomDirection();
    private Vector2d position;
    public int energy;
    public int[] genome;
    public int geneIndex;
    public int nextGene;
    public int childNumber=0;
    public int age=0;
    public int deathDay=-1;
    public int grassesEaten=0;
    public int toBirthEnergy;
    public int genomeLength;
    public int isChild=0;
    private ArrayList<IPositionChangeObserver> observers = new ArrayList<>();


    public Animal(Vector2d initialPosition, int energy, int genomeLength, int toBirthEnergy) {
        this.position = initialPosition;
        this.energy = energy;
        this.toBirthEnergy = toBirthEnergy;
        this.genomeLength = genomeLength;
        this.genome = new int[genomeLength];
        for (int i=0; i<genomeLength; i++) {
            this.genome[i] = randomInt(0, 7);
        }
        this.geneIndex = randomInt(0, genomeLength-1);
        this.nextGene = genome[geneIndex];
    }

    public Animal(Vector2d initialPosition, int energy, int genomeLength, int[] genome, int toBirthEnergy) {
        this.position = initialPosition;
        this.energy = energy;
        this.genome = genome;
        this.genomeLength = genomeLength;
        this.geneIndex = randomInt(0, genomeLength-1);
        this.nextGene = genome[geneIndex];
        this.toBirthEnergy = toBirthEnergy;
    }

    public Vector2d getPosition() {
        return position;
    }


    public MapDirection getOrientation() {
        return orientation;
    }

    public void setOrientation(MapDirection orientation) {
        this.orientation = orientation;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }


    public void addObserver(IPositionChangeObserver observer){
        observers.add(observer);
    }
    public void removeObserver(IPositionChangeObserver observer){
        observers.remove(observer);
    }


    public int randomInt(int min, int max) {
        return new Random().nextInt(max+1) + min;
    }

    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {
        for(IPositionChangeObserver observer : observers){
            observer.positionChanged(this, oldPosition, newPosition);
        }
    }
}
