package org.evo;

import org.evo.gui.App;

import java.util.ArrayList;
import java.util.Random;


public class SimulationEngine implements Runnable{

    public AbstractWorldMap map;
    public App app;
    private int moveDelay;
    private int daysLimit;
    private int grassNutritionalValue;
    private int dailyGrassGrowth;
    private int mutationType;
    private int minBreedingEnergy;
    private int minChildMutations;
    private int maxChildMutations;
    private int behaviourType;
    public int running=1;
    public int deathCounter = 0;
    public int daysLivedSum = 0;
    public int currentDay=0;

    public SimulationEngine(App app, AbstractWorldMap map, int grassNutritionalValue, int dailyGrassGrowth, int mutationType, int minBreedingEnergy,
                            int minChildMutations, int maxChildMutations, int behaviourType, int daysLimit, int moveDelay) {
        this.map = map;
        this.grassNutritionalValue = grassNutritionalValue;
        this.dailyGrassGrowth = dailyGrassGrowth;
        this.mutationType = mutationType;
        this.minBreedingEnergy = minBreedingEnergy;
        this.minChildMutations = minChildMutations;
        this.maxChildMutations = maxChildMutations;
        this.behaviourType = behaviourType;
        this.app = app;
        this.daysLimit = daysLimit;
        this.moveDelay = moveDelay;
        }

    @Override
    public void run() {
        int day;
        for (day=0; day<daysLimit; day++) {
            if (map.animalsList.size()==0) {
                break;
            }
            currentDay = day;
            while (running==0) {
                try {
                    Thread.sleep(moveDelay);
                } catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
            try {
                this.app.semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (app.trackedAnimal!=null) {
                this.app.updateTrackedAnimalValues();
            }
            this.app.updateTrackedValues();
            removeDeadAnimals(map, day);
            moveAnimals(map);
            eatGrasses(map);
            spawnAnimals(map);
            spawnGrasses(map, dailyGrassGrowth);

            this.app.semaphore.release();
            try {
                Thread.sleep(moveDelay);
            } catch (InterruptedException e) {
                System.out.println(e.toString());
            }
            this.app.drawMapGrid();
        }

    }

    public void removeDeadAnimals(AbstractWorldMap map, int day) {
        ArrayList<Animal> toRemove = new ArrayList<>();
        try {
            for (Animal animal : map.animalsList) {
                if (animal.energy <= 0) {
                    animal.deathDay = day;
                    deathCounter+=1;
                    daysLivedSum+=day;
                    toRemove.add(animal);
                }
            }
        } catch(Exception e) {
            System.out.println(e.toString());
        }
        for (Animal animal: toRemove) {
            map.removeAnimal(animal);
        }
    }

    public void moveAnimals(AbstractWorldMap map) {
        for (Animal animal: map.animalsList) {
            map.rotateAnimal(animal, behaviourType);
            map.moveAnimal(animal);
            animal.age+=1;
            animal.energy-=1;
        }
    }

    public void eatGrasses(AbstractWorldMap map) {
        for (Vector2d position: map.animalsAt.keySet()) {
            if (map.grasses.containsKey(position)) {
                Animal bestAnimal = map.getBestAnimal(position);
                bestAnimal.energy+=grassNutritionalValue;
                bestAnimal.grassesEaten+=1;
                map.grasses.remove(position);
                map.grassesCount-=1;
            }
        }
    }

    public void spawnAnimals(AbstractWorldMap map) {
        for (Vector2d position: map.animalsAt.keySet()) {
            if (map.animalsAt.get(position).size()>1) {
                ArrayList<Animal> bestPair = map.get2BestAnimals(position);
                if (bestPair.get(0).energy<minBreedingEnergy || bestPair.get(1).energy<minBreedingEnergy) {
                    continue;
                }
                int[] childGenome = mixGenomes(bestPair.get(0), bestPair.get(1));
                Animal child = new Animal(position, 2*bestPair.get(0).toBirthEnergy, childGenome.length, childGenome, bestPair.get(0).toBirthEnergy);
                if (!map.animalsAt.containsKey(position)) {
                    map.animalsAt.put(position, new ArrayList<Animal>());
                }
                map.animalsAt.get(position).add(child);
                map.animalsList.add(child);
                child.addObserver(map);
                bestPair.get(0).childNumber+=1;
                bestPair.get(1).childNumber+=1;
                bestPair.get(0).energy-=bestPair.get(0).toBirthEnergy;
                bestPair.get(1).energy-=bestPair.get(1).toBirthEnergy;
                mutateGenome(child);
                child.isChild=1;
                map.animalsAlive+=1;
            }
        }
    }

    public void spawnGrasses(AbstractWorldMap map, int grassesToSpawn) {
        int equatorLowerBound = (int)((map.height+1-round((map.height+1)*0.2))/2);
        int equatorUpperBound = equatorLowerBound+round((map.height+1)*0.2)-1;
        int grassesSpawned=0;
        int numOfTries=0;
        while (grassesSpawned<grassesToSpawn && numOfTries<grassesToSpawn*50) {
            Vector2d newGrassPosition = new Vector2d(0, 0);
            if (getRandomNumber(0, 4) == 0) {
                do {
                    newGrassPosition.x = getRandomNumber(0, map.width);
                    newGrassPosition.y = getRandomNumber(0, map.height);
                } while (newGrassPosition.y>=equatorLowerBound && newGrassPosition.y<=equatorUpperBound);
            }
            else {
                do {
                    newGrassPosition.x = getRandomNumber(0, map.width);
                    newGrassPosition.y = getRandomNumber(0, map.height);
                } while (newGrassPosition.y<equatorLowerBound || newGrassPosition.y>equatorUpperBound);
            }
            if (!map.grasses.containsKey(newGrassPosition)) {
                grassesSpawned+=1;
                map.grasses.put(newGrassPosition, new Grass(newGrassPosition, grassNutritionalValue));
            }
            else {
                numOfTries+=1;
            }
        }
        map.grassesCount+=grassesSpawned;
    }

    public int[] mixGenomes(Animal parent1, Animal parent2) {
        Animal stronger;
        Animal weaker;
        int[] newGenome = new int[parent1.genomeLength];
        if (parent1.energy > parent2.energy) {
            stronger = parent1;
            weaker = parent2;
        }
        else {
            stronger = parent2;
            weaker = parent1;
        }
        int strongerGenesNumber = (int) (newGenome.length*stronger.energy/(stronger.energy+weaker.energy));
        int i=0;
        if (getRandomNumber(0, 1)==0) {
            while (i<strongerGenesNumber) {
                newGenome[i] = stronger.genome[i];
                i++;
            }
            while (i<weaker.genomeLength) {
                newGenome[i] = weaker.genome[i];
                i++;
            }
        }
        else {
            while (i<strongerGenesNumber) {
                newGenome[stronger.genomeLength-1-i] = stronger.genome[stronger.genomeLength-1-i];
                i++;
            }
            while (i<weaker.genomeLength) {
                newGenome[weaker.genomeLength-1-i] = weaker.genome[weaker.genomeLength-1-i];
                i++;
            }
        }
        return newGenome;
    }

    public void mutateGenome(Animal animal) {
        int mutationNumber = getRandomNumber(minChildMutations, maxChildMutations);
        int i=0;
        int idxToMutate;
        while (i<mutationNumber) {
            idxToMutate = getRandomNumber(0, animal.genomeLength-1);
            if (mutationType==0) {
                animal.genome[idxToMutate] = getRandomNumber(0, 7);
            }
            else {
                if (getRandomNumber(0, 1)==0) {
                    animal.genome[idxToMutate] = mod(animal.genome[idxToMutate]+1, animal.genomeLength);
                }
                else {
                    animal.genome[idxToMutate] = mod(animal.genome[idxToMutate]-1, animal.genomeLength);
                }
            }
            i++;
        }
    }

    public int getAverageLifeSpan() {
        if (deathCounter==0) {
            return 0;
        }
        return (int)daysLivedSum/deathCounter;
    }
    public int getRandomNumber(int min, int max) {
        return new Random().nextInt(max+1) + min;
    }

    public int round(double x) {
        int y = (int) x;
        if (x-y>=0.5) {
            return y+1;
        }
        return y;
    }

    public int mod(int n, int m) {
        return (((n % m) + m) % m);
    }
}
