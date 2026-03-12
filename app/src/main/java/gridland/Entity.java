package gridland;

import processing.core.PApplet;

public class Entity {

    int maxAgeOfHuman = 200;
    int maxAgeOfSheep = 50;
    int food=0;
    int maxStoreFoodForSheep=5;
    int maxStoreFoodForHuman=5;
    EntityType type;
    int age=0;
    int x, y;


    Entity(EntityType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;

    }

    public boolean isCanReproduce() {
        if (type == EntityType.HUMAN) {
            if (food>maxStoreFoodForHuman) {
                return true;
            }
        } else if (type == EntityType.SHEEP) {
            if (food>maxStoreFoodForSheep) {
                return true;
            }
        }
        return false;
    }

    public EntityType getType() {
        return type;
    }






    void clearFood(){
        food=0;
    }

    boolean tooOld() {
        switch (type) {
            case HUMAN:
                if (age > maxAgeOfHuman) {
                    return true;
                }
                break;
            case SHEEP:
                if (age > maxAgeOfSheep) {
                    return true;
                }
                break;
        }
        return false;
    }

    void eat(){
        food++;
    }
}