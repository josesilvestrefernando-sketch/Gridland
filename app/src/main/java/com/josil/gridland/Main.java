package com.josil.gridland;


import gridland.Gridland;
import processing.core.PApplet;

public class Main extends PApplet {


    Gridland gridland;

    public void settings() {

        fullScreen();
    }


    public void setup() {
        int CELL_SIZE = 30;
        int totalwidth = width / CELL_SIZE;
        int tolaheight = height / CELL_SIZE;
        int GRID_SIZE_W = totalwidth;
        int GRID_SIZE_H = (int) (tolaheight * .8f);

        gridland = new Gridland(this, GRID_SIZE_W, GRID_SIZE_H, CELL_SIZE);
    }

    public void draw() {
        gridland.draw();
    }


}
