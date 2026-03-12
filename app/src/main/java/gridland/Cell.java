package gridland;

public class Cell {
    int x, y;
    boolean hasGrass = false;
    Tree tree = null;
    Entity entity = null;
    Cell(int x, int y) {
        this.x = x; this.y = y;
    }
    Cell(float x, float y) {
        this.x = (int) x; this.y = (int) y;
    }
    boolean noEntity(){
        return entity==null;
    }
    boolean hasSheep(){
        if (noEntity()==false){
            return entity.getType()==EntityType.SHEEP;
        }
        return false;
    }
    void clearGrass(){
        hasGrass=false;
    }
    void clearEntity(){
        entity=null;
    }
    void clearTree(){
        tree=null;
    }
    Entity getEntity(){
        return entity;
    }
    boolean noTree(){
        return tree==null;
    }
    boolean isHasGrass(){
        return hasGrass;
    }
    boolean hasTree(){
        return tree!=null;
    }
    boolean hasNoTree(){
        return tree==null;
    }
    boolean hasNoHuman(){
        if (entity==null){
            return true;
        }
        else if (entity.getType()==EntityType.SHEEP){
            return true;
        }
        else if (entity.getType()==EntityType.HUMAN){
            return false;
        }
        return true;

    }
}