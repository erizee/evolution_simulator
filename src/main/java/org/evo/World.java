package org.evo;


import javafx.application.Application;
import org.evo.gui.App;


import static java.lang.System.out;

public class World {


    public static void main(String[] args) {


        try {
            Application.launch(App.class);
        } catch(IllegalArgumentException ex) {
            out.println(ex.toString());
        }


    }
}