package org.evo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.out;


public abstract class AbstractWorldMap implements IPositionChangeObserver {
    protected int width;
    protected int height;
    public Map<Vector2d, ArrayList<Animal>> animalsAt = new HashMap<>();

    public Map<Vector2d, Grass> grasses = new ConcurrentHashMap<>();
    public ArrayList<Animal> animalsList = new ArrayList<>();
    public int animalsAlive = 0;
    public int grassesCount = 0;

    public AbstractWorldMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition) {
        animalsAt.get(oldPosition).remove(animal);
        if (animalsAt.get(oldPosition).size()==0) {
            animalsAt.remove(oldPosition);
        }
        if (!animalsAt.containsKey(newPosition)) {
            animalsAt.put(newPosition, new ArrayList<Animal>());
        }
        animalsAt.get(newPosition).add(animal);
    }

    public boolean canMoveTo(Vector2d position) {
        return position.follows(new Vector2d(0, 0)) && position.precedes(new Vector2d(width, height));
    }

    public boolean place(Animal animal) {
        if (canMoveTo(animal.getPosition())) {
            animalsList.add(animal);
            if (!animalsAt.containsKey(animal.getPosition())) {
                animalsAt.put(animal.getPosition(), new ArrayList<Animal>());
            }
            animalsAt.get(animal.getPosition()).add(animal);
            animalsAlive+=1;
            return true;
        }
        return false;
    }

    public void removeAnimal(Animal animal) {
        try {
            Vector2d position = animal.getPosition();
            if (animalsAt.containsKey(position)) {
                animalsAt.get(position).remove(animal);
                if (animalsAt.get(position).size() == 0) {
                    animalsAt.remove(position);
                }
            }
            animalsList.remove(animal);
            animalsAlive-=1;

        } catch(Exception e) {
            out.println(e.toString());
        }
    }

    public abstract void moveAnimal(Animal animal);

    public void rotateAnimal(Animal animal, int behaviourType) {
        animal.setOrientation(animal.getOrientation().rotate(animal.nextGene));
        if (behaviourType==0 || getRandomNumber(0, 4)!=0) {
            animal.geneIndex = (animal.geneIndex+1)%animal.genomeLength;
            animal.nextGene = animal.genome[animal.geneIndex];
        }
        else {
            animal.geneIndex = getRandomNumber(0, animal.genomeLength-1);
            animal.nextGene = animal.genome[animal.geneIndex];
        }

    }

    public Animal getBestAnimal(Vector2d position) {
        animalsAt.get(position).sort(new AnimalsComparator());
        return animalsAt.get(position).get(0);
    }

    public ArrayList<Animal> get2BestAnimals(Vector2d position) {
        animalsAt.get(position).sort(new AnimalsComparator());
        ArrayList<Animal> bestAnimals = new ArrayList<Animal>();
        bestAnimals.add(animalsAt.get(position).get(0));
        bestAnimals.add(animalsAt.get(position).get(1));
        return bestAnimals;
    }

    public int[] getDominantGenotype() {
        if (animalsList.size()==0) {
            return new int[0];
        }
        String[] dominants = new String[animalsList.size()];
        for (int i=0; i<animalsList.size(); i++) {
            dominants[i] = Arrays.toString(animalsList.get(i).genome);
        }
        Arrays.sort(dominants);
        String res = dominants[0];
        int c = 1;
        int mx = 0;
        for (int i=1; i<dominants.length; i++) {
            if (dominants[i].equals(dominants[i-1])) {
                c+=1;
            }
            else {
                c = 1;
            }
            if (c>mx) {
                mx = c;
                res = dominants[i];
            }

        }
        String[] items = res.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

        int[] results = new int[items.length];

        for (int i = 0; i < items.length; i++) {
            try {
                results[i] = Integer.parseInt(items[i]);
            } catch (NumberFormatException nfe) {
            };
        }
        return results;
    }

    public int getFreeFieldsNumber() {
        int all = (height+1)*(width+1);
        int taken = animalsAt.size();
        for (Vector2d position: grasses.keySet()) {
            if (!animalsAt.containsKey(position)) {
                taken+=1;
            }
        }
        return all-taken;
    }

    public int getAverageEnergy() {
        if (animalsAlive==0 || animalsList.size()==0) {
            return 0;
        }
        float energySum=0;
        for (Animal animal: animalsList) {
            energySum+=animal.energy;
        }
        return (int)energySum/animalsList.size();
    }



    public int getRandomNumber(int min, int max) {
        return new Random().nextInt(max+1) + min;
    }

    public Object objectAt(Vector2d position) {
        if (animalsAt.containsKey(position)) {
            return getBestAnimal(position);
        }
        return null;
    }
}
