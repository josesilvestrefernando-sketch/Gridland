package gridland;

import processing.core.PApplet;

public  class Tree {
    PApplet pApplet;
    int age = 1;
    int maxAge=15;
    int food=0;
    int maxFood=5;
    Tree(PApplet pApplet1){
        pApplet=pApplet1;
    }
    void grow() {
        age++;
        food++;
    }
    boolean isMature(){
        if (food > maxFood  && dead()==false){
            return true;
        }
        return false;
    }
    boolean dead(){
        return age>maxAge;
    }
    void sap(){
        maxFood=maxFood*2;
        food=0;
    }
}