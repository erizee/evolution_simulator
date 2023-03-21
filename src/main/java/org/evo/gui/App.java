package org.evo.gui;

import org.evo.*;

import static java.lang.System.exit;
import static java.lang.System.out;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Semaphore;



public class App extends Application{

    public int width;
    public int height;
    public int mapType;
    public int initialGrassesNumber;
    public int grassNutritionalValue;
    public int dailyGrassGrowth;
    public int grassSpawnType;
    public int initialAnimalsNumber;
    public int animalInitialEnergy;
    public int minBreedingEnergy;
    public int toBirthEnergy;
    public int minChildMutations;
    public int maxChildMutations;
    public int mutationType;
    public int genomeLength;
    public int behaviourType;
    public int daysLimit;
    public int moveDelay;
    public int saveStats;
    public Thread engineThread;
    public Semaphore semaphore;
    public SimulationEngine engine;
    public String state = "";
    private AbstractWorldMap map;
    public Animal trackedAnimal;

    public final GridPane mapGrid = new GridPane();

    public ArrayList<GuiElementBox> grassImages;
    public ArrayList<GuiElementBox> strongAnimalImages;
    public ArrayList<GuiElementBox> weakAnimalImages;

    public Text trackedAnimalGenome = new Text();
    public Text trackedAnimalGeneIndex = new Text();
    public Text trackedAnimalCurrentEnergy = new Text();
    public Text trackedAnimalGrassesEaten = new Text();
    public Text trackedAnimalChildNb = new Text();
    public Text trackedAnimalAge = new Text();
    public Text trackedAnimalDeathDay = new Text();
    public Text trackedAnimalPosition = new Text();
    public Text tAnimalsAlive = new Text("alive animals: ");
    public Text tGrassesCount = new Text("grasses number: ");
    public Text tFreeFields = new Text("free fields: ");
    public Text tDominantGenotype = new Text("dominant genotype: ");
    public Text tAverageEnergy = new Text("average energy: ");
    public Text tAverageLifeSpan = new Text("average lifespan: ");
    public Text tSimulationDay = new Text("day of simulation: ");

    @Override
    public void start(Stage primaryStage) {
        TextField tfWidth = new TextField(String.valueOf(this.width));
        tfWidth.textProperty().addListener((observable, oldValue, newValue) -> this.width = Integer.parseInt(newValue));
        TextField tfHeight = new TextField(String.valueOf(this.height));
        tfHeight.textProperty().addListener((observable, oldValue, newValue) -> this.height = Integer.parseInt(newValue));
        TextField tfMapType = new TextField(String.valueOf(this.mapType));
        tfMapType.textProperty().addListener((observable, oldValue, newValue) -> this.mapType = Integer.parseInt(newValue));
        TextField tfInitialGrassesNumber = new TextField(String.valueOf(this.initialGrassesNumber));
        tfInitialGrassesNumber.textProperty().addListener((observable, oldValue, newValue) -> this.initialGrassesNumber = Integer.parseInt(newValue));
        TextField tfGrassNutritionalValue = new TextField(String.valueOf(this.grassNutritionalValue));
        tfGrassNutritionalValue.textProperty().addListener((observable, oldValue, newValue) -> this.grassNutritionalValue = Integer.parseInt(newValue));
        TextField tfDailyGrassGrowth = new TextField(String.valueOf(this.dailyGrassGrowth));
        tfDailyGrassGrowth.textProperty().addListener((observable, oldValue, newValue) -> this.dailyGrassGrowth = Integer.parseInt(newValue));
        TextField tfInitialAnimalsNumber = new TextField(String.valueOf(this.initialAnimalsNumber));
        tfInitialAnimalsNumber.textProperty().addListener((observable, oldValue, newValue) -> this.initialAnimalsNumber = Integer.parseInt(newValue));
        TextField tfAnimalInitialEnergy = new TextField(String.valueOf(this.animalInitialEnergy));
        tfAnimalInitialEnergy.textProperty().addListener((observable, oldValue, newValue) -> this.animalInitialEnergy = Integer.parseInt(newValue));
        TextField tfMinBreedingEnergy = new TextField(String.valueOf(this.minBreedingEnergy));
        tfMinBreedingEnergy.textProperty().addListener((observable, oldValue, newValue) -> this.minBreedingEnergy = Integer.parseInt(newValue));
        TextField tfToBirthEnergy = new TextField(String.valueOf(this.toBirthEnergy));
        tfToBirthEnergy.textProperty().addListener((observable, oldValue, newValue) -> this.toBirthEnergy = Integer.parseInt(newValue));
        TextField tfMinChildMutations = new TextField(String.valueOf(this.minChildMutations));
        tfMinChildMutations.textProperty().addListener((observable, oldValue, newValue) -> this.minChildMutations = Integer.parseInt(newValue));
        TextField tfMaxChildMutations = new TextField(String.valueOf(this.maxChildMutations));
        tfMaxChildMutations.textProperty().addListener((observable, oldValue, newValue) -> this.maxChildMutations = Integer.parseInt(newValue));
        TextField tfMutationType = new TextField(String.valueOf(this.mutationType));
        tfMutationType.textProperty().addListener((observable, oldValue, newValue) -> this.mutationType = Integer.parseInt(newValue));
        TextField tfGenomeLength = new TextField(String.valueOf(this.genomeLength));
        tfGenomeLength.textProperty().addListener((observable, oldValue, newValue) -> this.genomeLength = Integer.parseInt(newValue));
        TextField tfBehaviourType = new TextField(String.valueOf(this.behaviourType));
        tfBehaviourType.textProperty().addListener((observable, oldValue, newValue) -> this.behaviourType = Integer.parseInt(newValue));
        TextField tfDaysLimit = new TextField(String.valueOf(this.daysLimit));
        tfDaysLimit.textProperty().addListener((observable, oldValue, newValue) -> this.daysLimit = Integer.parseInt(newValue));
        TextField tfMoveDelay = new TextField(String.valueOf(this.moveDelay));
        tfMoveDelay.textProperty().addListener((observable, oldValue, newValue) -> this.moveDelay = Integer.parseInt(newValue));
        TextField tfSaveStats = new TextField(String.valueOf(this.saveStats));
        tfSaveStats.textProperty().addListener((observable, oldValue, newValue) -> this.saveStats = Integer.parseInt(newValue));


        primaryStage.setTitle("Evolution simulation");
        Button start = new Button("Start");
        Button pause = new Button("Pause");
        Button resume = new Button("Resume");


        ListView<String> scrollableList = new ListView<String>();
        ObservableList<String> scrollableListInfo = FXCollections.observableArrayList("pause to choose animal to track");
        scrollableList.setItems(scrollableListInfo);


        start.setOnAction(action -> {
            if (!parameterCheck()) {
                out.println("#####GIVE CORRECT PARAMETERS#####");
                exit(0);
            }
            if (mapType==0) {
                this.map = new EarthMap(this.width, this.height);
            }
            else {
                this.map = new PortalMap(this.width, this.height);
            }
            for(int i = 0; i<this.initialAnimalsNumber; i++){
                Animal newAnimal = new Animal(new Vector2d(getRandomNumber(0, this.width), getRandomNumber(0, this.height)), this.animalInitialEnergy,
                        this.genomeLength, this.toBirthEnergy);
                newAnimal.addObserver(map);
                this.map.place(newAnimal);
            }

            for(int col = 0; col <= this.width; col++){
                mapGrid.getColumnConstraints().add(new ColumnConstraints(20));
            }
            for(int row = 0; row <= this.height; row++){
                mapGrid.getRowConstraints().add(new RowConstraints(20));
            }
            for(int row = 0; row <= this.height; row++){
                for(int col = 0; col <= this.width; col++){
                    Label toAdd = new Label(" ");
                    mapGrid.add(toAdd, col, row);
                }
            }
            this.strongAnimalImages = new ArrayList<>(2*(1+width)*(1+height));
            this.weakAnimalImages = new ArrayList<>(2*(1+width)*(1+height));
            this.grassImages = new ArrayList<>(2*(1+width)*(1+height));
            for(int i = 0; i<=2*(width+1)*(height+1); i++){
                this.grassImages.add(new GuiElementBox("src/main/resources/grass.png"));
                this.strongAnimalImages.add(new GuiElementBox("src/main/resources/red.png"));
                this.weakAnimalImages.add(new GuiElementBox("src/main/resources/blue.png"));
            }
            this.engine = new SimulationEngine(this, this.map, grassNutritionalValue, dailyGrassGrowth, mutationType, minBreedingEnergy,
                    minChildMutations, maxChildMutations, behaviourType, daysLimit, moveDelay);
            this.engine.spawnGrasses(map, initialGrassesNumber);
            this.engineThread = new Thread(engine);
            this.semaphore = new Semaphore(1);
            engineThread.start();


        });
        pause.setOnAction(act -> {
            this.engine.running = 0;
            scrollableList.getItems().clear();
            Map<Integer, Animal> temp = new HashMap<>(this.map.animalsList.size());
            Set<Vector2d> keys = this.map.animalsAt.keySet();
            int p = 0;
            int[] dominantGenotype = map.getDominantGenotype();
            for(Vector2d loc : keys){
                for(Animal animal : this.map.animalsAt.get(loc)){
                    temp.put(p, animal);
                    p++;
                    if (Arrays.equals(animal.genome, dominantGenotype)) {
                        scrollableList.getItems().add("D "+loc + ", energy: " + animal.energy + ", orientation: " + animal.getOrientation());
                    }
                    else {
                        scrollableList.getItems().add(loc + ", energy: " + animal.energy + ", orientation: " + animal.getOrientation());
                    }
                }
            }
            scrollableList.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int p = scrollableList.getSelectionModel().getSelectedIndex();
                    trackedAnimal = temp.get(p);
                    trackedAnimalGenome.setText("genome: " + Arrays.toString(trackedAnimal.genome));
                    trackedAnimalGeneIndex.setText("activated gene idx: " + trackedAnimal.geneIndex);
                    trackedAnimalCurrentEnergy.setText("energy: " + trackedAnimal.energy);
                    trackedAnimalGrassesEaten.setText("eaten grasses: " + trackedAnimal.grassesEaten);
                    trackedAnimalChildNb.setText("childrens: " + trackedAnimal.childNumber);
                    trackedAnimalAge.setText("age: " + trackedAnimal.age);
                    trackedAnimalDeathDay.setText("death day: " + trackedAnimal.deathDay);
                    trackedAnimalPosition.setText("position: " + trackedAnimal.getPosition().toString());
                }
            });
        });
        resume.setOnAction(act -> {
            this.engine.running=1;
            scrollableList.getItems().clear();
            scrollableList.getItems().add("pause to choose animal to track");
        });


        VBox chosenParams = new VBox(new Text("Current parameters:"),
                new HBox(new Text("width: "), tfWidth),
                new HBox(new Text("height: "), tfHeight),
                new HBox(new Text("map type: "), tfMapType),
                new HBox(new Text("grasses: "), tfInitialGrassesNumber),
                new HBox(new Text("grass energy: "), tfGrassNutritionalValue),
                new HBox(new Text("daily grasses: "), tfDailyGrassGrowth),
                new HBox(new Text("animals: "), tfInitialAnimalsNumber),
                new HBox(new Text("initial energy: "), tfAnimalInitialEnergy),
                new HBox(new Text("to breed energy: "), tfMinBreedingEnergy),
                new HBox(new Text("to birth energy: "), tfToBirthEnergy),
                new HBox(new Text("min mutations: "), tfMinChildMutations),
                new HBox(new Text("max mutations: "), tfMaxChildMutations),
                new HBox(new Text("mutation type: "), tfMutationType),
                new HBox(new Text("genome length: "), tfGenomeLength),
                new HBox(new Text("behaviour type: "), tfBehaviourType),
                new HBox(new Text("days limit: "), tfDaysLimit),
                new HBox(new Text("move delay: "), tfMoveDelay),
                new HBox(new Text("save stats: "), tfSaveStats),
                start,
                pause,
                resume);
        Scene scene = new Scene(new VBox(new HBox(chosenParams, mapGrid, new VBox(new HBox(tAnimalsAlive),new HBox( tGrassesCount), new HBox(tFreeFields), new HBox(tDominantGenotype),
                new HBox(tAverageEnergy), new HBox(tAverageLifeSpan), new HBox(tSimulationDay), new HBox(scrollableList,
                new VBox(trackedAnimalGenome, trackedAnimalGeneIndex, trackedAnimalCurrentEnergy, trackedAnimalGrassesEaten,
                trackedAnimalChildNb, trackedAnimalAge, trackedAnimalDeathDay, trackedAnimalPosition))))
                 ), 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void updateTrackedAnimalValues() {
        trackedAnimalGeneIndex.setText("activated gene idx: " + trackedAnimal.geneIndex);
        trackedAnimalCurrentEnergy.setText("energy: " + trackedAnimal.energy);
        trackedAnimalGrassesEaten.setText("eaten grasses: " + trackedAnimal.grassesEaten);
        trackedAnimalChildNb.setText("childrens: " + trackedAnimal.childNumber);
        trackedAnimalAge.setText("age: " + trackedAnimal.age);
        trackedAnimalDeathDay.setText("death day: " + trackedAnimal.deathDay);
        trackedAnimalPosition.setText("position: " + trackedAnimal.getPosition().toString());
    }

    public void updateTrackedValues() {
        tAnimalsAlive.setText("alive animals: "+map.animalsAlive);
        tGrassesCount.setText("grasses number: "+map.grassesCount);
        tFreeFields.setText("free fields: "+map.getFreeFieldsNumber());
        tDominantGenotype.setText("dominant genotype: " + Arrays.toString(map.getDominantGenotype()));
        tAverageEnergy.setText("average energy: "+map.getAverageEnergy());
        tAverageLifeSpan.setText("average lifespan: "+engine.getAverageLifeSpan());
        tSimulationDay.setText("day of simulation: "+engine.currentDay);
        if (saveStats==1) {
            try {
                PrintWriter stateReport = new PrintWriter(new File("state_report.csv"));
                state += String.valueOf(map.animalsAlive) + ',' + map.grassesCount + ',' + map.getFreeFieldsNumber() + ',' +
                        Arrays.toString(map.getDominantGenotype())+','+map.getAverageEnergy()+','+engine.getAverageLifeSpan()+','+engine.currentDay+'\n';
                stateReport.write(state);
                stateReport.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void drawMapGrid(){
        Platform.runLater(() -> {
            try {
                this.semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int g = 0;
            int sa = 0;
            int wa = 0;
            mapGrid.getChildren().clear();
            Set<Vector2d> keys = map.grasses.keySet();
            for(Vector2d position : keys){
                Label toAdd = new Label();
                toAdd.setGraphic(this.grassImages.get(g).imageView);
                mapGrid.add(toAdd, position.x, position.y);
                g++;
            }

            keys = map.animalsAt.keySet();
            for(Vector2d position : keys){
                if (map.animalsAt.get(position).size()==0) {
                    continue;
                }
                if(map.getBestAnimal(position).energy < 5*animalInitialEnergy){
                    Label toAdd = new Label();
                    toAdd.setGraphic(this.weakAnimalImages.get(wa).imageView);
                    mapGrid.add(toAdd, position.x, position.y);
                    wa++;
                }
                else{
                    Label toAdd = new Label();
                    toAdd.setGraphic(this.strongAnimalImages.get(sa).imageView);
                    mapGrid.add(toAdd, position.x, position.y);
                    sa++;
                }
            }
            this.semaphore.release();
        });
    }

    public void init() {
        String[] args = {"10", "10", "0", "5", "30", "1", "0", "6", "20", "60", "30", "0", "2", "0", "4", "0", "1000", "200", "0"};
        width = Integer.parseInt(args[0]);
        height = Integer.parseInt(args[1]);
        mapType = Integer.parseInt(args[2]);
        initialGrassesNumber = Integer.parseInt(args[3]);
        grassNutritionalValue = Integer.parseInt(args[4]);
        dailyGrassGrowth = Integer.parseInt(args[5]);
        grassSpawnType = Integer.parseInt(args[6]);
        initialAnimalsNumber = Integer.parseInt(args[7]);
        animalInitialEnergy = Integer.parseInt(args[8]);
        minBreedingEnergy = Integer.parseInt(args[9]);
        toBirthEnergy = Integer.parseInt(args[10]);
        minChildMutations = Integer.parseInt(args[11]);
        maxChildMutations = Integer.parseInt(args[12]);
        mutationType = Integer.parseInt(args[13]);
        genomeLength = Integer.parseInt(args[14]);
        behaviourType = Integer.parseInt(args[15]);
        daysLimit = Integer.parseInt(args[16]);
        moveDelay = Integer.parseInt(args[17]);
        saveStats = Integer.parseInt(args[18]);

        if (mapType==0) {
            this.map = new EarthMap(this.width, this.height);
        }
        else {
            this.map = new PortalMap(this.width, this.height);
        }
        this.strongAnimalImages = new ArrayList<>(2*(1+width)*(1+height));
        this.weakAnimalImages = new ArrayList<>(2*(1+width)*(1+height));
        this.grassImages = new ArrayList<>(2*(1+width)*(1+height));

        this.trackedAnimal = null;
    }

    public boolean parameterCheck() {
        out.println("*****CHECKING PARAMETERS CORRECTNESS****");
        if (width<5 || height<5 || width>30 || height>30) {
            out.println("Width and Height must be in range (5, 30)!");
            return false;
        }
        if (mapType!=0 && mapType!=1) {
            out.println("MapType must be 0 or 1");
            out.println(mapType);
            return false;
        }
        if (initialGrassesNumber>10 || initialGrassesNumber<0) {
            out.println("InitialGrassesNumber must be in range (0,10)");
            return false;
        }
        if (grassNutritionalValue<1 || grassNutritionalValue>100) {
            out.println("#####GIVE CORRECT PARAMETERS#####");
            out.println("GrassNutritionalValue must be in range (1, 100)");
            return false;
        }
        if (dailyGrassGrowth<1 || dailyGrassGrowth>20) {
            out.println("DailyGrassGrowth must be in range (1,20)");
            return false;
        }
        if (initialAnimalsNumber<1 || initialAnimalsNumber>10) {
            out.println("InitialAnimalsNumber must be in range (1, 10)");
            return false;
        }
        if (animalInitialEnergy<5 || animalInitialEnergy>100) {
            out.println("AnimalInitialEnergy must be in range (5,100)");
            return false;
        }
        if (minBreedingEnergy<20 || minBreedingEnergy>100) {
            out.println("MinBreedingEnergy must be in range (20,100)");
            return false;
        }
        if (toBirthEnergy<10 || toBirthEnergy>60 || toBirthEnergy>minBreedingEnergy) {
            out.println("ToBirthEnergy must be in range (10, 60) and not greater than minBreedingEnergy");
        }
        if (minChildMutations<0 || minChildMutations>15) {
            out.println("MinChildMutations must be in range (0,15)");
            return false;
        }
        if (maxChildMutations<0 || maxChildMutations>15 || maxChildMutations<minChildMutations) {
            out.println("MaxChildMutations must be in range (0,15) and not greater than minChildMutations");
            return false;
        }
        if (mutationType!=0 && mutationType!=1) {
            out.println("Mutation type must be 0 or 1");
        }
        if (genomeLength<2 || genomeLength>32) {
            out.println("GenomeLength must be in range (2,32)");
            return false;
        }
        if (behaviourType!=0 && behaviourType!=1) {
            out.println("BehaviourType must be 0 or 1");
        }
        if (daysLimit<10 || daysLimit>10000) {
            out.println("DaysLimit must be in range (10,10000)");
            return false;
        }
        if (moveDelay<100 || moveDelay>1000) {
            out.println("MoveDelay must be in range (100,1000)");
            return false;
        }
        if (saveStats!=0 && saveStats!=1) {
            out.println("SaveStats must be 0 or 1");
            return false;
        }
        out.println("*****PARAMETERS CORRECT****");
        return true;
    }

    public int getRandomNumber(int min, int max) {
        return new Random().nextInt(max+1) + min;
    }
}
