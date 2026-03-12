package gridland;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;

public class Gridland {
    PApplet pApplet;
    int GRID_SIZE_H;
    int GRID_SIZE_W;
    int CELL_SIZE;
    int WIDTH;
    int HEIGHT;


    Cell[][] grid;
    ArrayList<Entity> humans = new ArrayList<>();
    ArrayList<Entity> sheeps = new ArrayList<>();


    public Gridland(PApplet pApplet, int gridsizew, int gridsizeh, int cellsize) {
        this.pApplet = pApplet;
        configureGrid(gridsizew, gridsizeh, cellsize);

        initGrid();
        pApplet.frameRate(3);

    }


    ArrayList<PVector> getCellsInRadius(int x, int y, int radius) {
        ArrayList<PVector> cells = new ArrayList<>();
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < GRID_SIZE_W && ny >= 0 && ny < GRID_SIZE_H) {
                    cells.add(new PVector(nx, ny));
                }
            }
        }
        return cells;
    }

    void spreadTrees() {
        ArrayList<PVector> saplings = new ArrayList<>();
        for (int y = 0; y < GRID_SIZE_H; y++) {
            for (int x = 0; x < GRID_SIZE_W; x++) {
                Cell c = grid[y][x];
                if (c.hasTree()) {
                    c.tree.grow();
                    if (c.tree.isMature()) {
                        ArrayList<PVector> nearby = getCellsInRadius(x, y, 2);
                        Collections.shuffle(nearby);
                        for (int i = 0; i < nearby.size(); i++) {
                            PVector p = nearby.get(i);

                            Cell candidate = grid[(int) p.y][(int) p.x];
                            if (candidate.hasNoTree() && candidate.noEntity()) {
                                saplings.add(p);
                                c.tree.sap();
                                //PApplet.println((int) y,(int) x,grid[y][(int) x].tree.age);
                                i = nearby.size(); // plant only one sapling per tree per tick
                            }
                        }
                    }
                }
            }
        }
        for (PVector p : saplings) {
            grid[(int) p.y][(int) p.x].tree = new Tree(pApplet);
            grid[(int) p.y][(int) p.x].clearGrass();


        }
    }

    void configureGrid(int gridsizew, int gridsizeh, int cellsize) {
        GRID_SIZE_H = gridsizeh;
        GRID_SIZE_W = gridsizew;
        CELL_SIZE = cellsize;
        WIDTH = GRID_SIZE_W * CELL_SIZE;
        HEIGHT = GRID_SIZE_H * CELL_SIZE;
        grid = new Cell[GRID_SIZE_H][GRID_SIZE_W];

    }


    void initGrid() {

        for (int y = 0; y < GRID_SIZE_H; y++) {
            for (int x = 0; x < GRID_SIZE_W; x++) {
                grid[y][x] = new Cell(x, y);
            }
        }

        grid[5][5].hasGrass = true;
        grid[GRID_SIZE_H - 6][5].hasGrass = true;
        grid[5][GRID_SIZE_W - 6].hasGrass = true;
        grid[GRID_SIZE_H - 6][GRID_SIZE_W - 6].hasGrass = true;

        grid[0][0].tree = new Tree(pApplet);
        grid[GRID_SIZE_H - 1][0].tree = new Tree(pApplet);
        grid[0][GRID_SIZE_W - 1].tree = new Tree(pApplet);
        grid[GRID_SIZE_H - 1][GRID_SIZE_W - 1].tree = new Tree(pApplet);


        addEntity(EntityType.SHEEP, 10, 10);
        addEntity(EntityType.SHEEP, GRID_SIZE_W - 11, 10);
        addEntity(EntityType.SHEEP, 10, GRID_SIZE_H - 11);
        addEntity(EntityType.SHEEP, GRID_SIZE_W - 11, GRID_SIZE_H - 11);

        addEntity(EntityType.HUMAN, 15, 15);
        addEntity(EntityType.HUMAN, GRID_SIZE_W - 16, GRID_SIZE_H - 16);

    }


    void addEntity(EntityType type) {

        int x = pApplet.floor(pApplet.random(GRID_SIZE_W));
        int y = pApplet.floor(pApplet.random(GRID_SIZE_H));

        Entity e = new Entity(type, x, y);
        grid[y][x].entity = e;
        if (type == EntityType.SHEEP) {
            sheeps.add(e);
        } else {
            humans.add(e);
        }


    }

    void addEntity(EntityType type, int x1, int y1) {

        int x = x1;
        int y = y1;

        Entity e = new Entity(type, x, y);
        grid[y][x].entity = e;
        if (type == EntityType.SHEEP) {
            sheeps.add(e);
        } else {
            humans.add(e);
        }


    }

    void updateEntities() {
        spreadGrass();
        spreadTrees();
        updateHumans();
        updateSheep();

    }

    void spreadGrass() {
        ArrayList<PVector> newGrass = new ArrayList<>();
        for (int y = 0; y < GRID_SIZE_H; y++) {
            for (int x = 0; x < GRID_SIZE_W; x++) {
                Cell c = grid[y][x];
                if (c.hasGrass) {
                    ArrayList<PVector> adj = getAdjacent(x, y);
                    for (PVector p : adj) {
                        Cell target = grid[(int) p.y][(int) p.x];
                        if (!grid[(int) p.y][(int) p.x].hasGrass && pApplet.random(1) < 0.15
                                && grid[(int) p.y][(int) p.x].hasNoTree() && target.noEntity()) {
                            newGrass.add(p);
                        }
                    }
                }
            }
        }
        for (PVector p : newGrass) {
            grid[(int) p.y][(int) p.x].hasGrass = true;
        }
    }


    void updateSheep() {

        for (int i = 0; i < sheeps.size(); i++) {
            Entity s = sheeps.get(i);
            s.age++;

            if (s.tooOld()) {
                Cell cell1 = getCell(s.x, s.y);
                cell1.clearEntity();
                sheeps.remove(i);
                cell1.clearGrass();


            } else {
                wanderSheep(s);

            }

        }
    }


    void wander(Entity e) {
        ArrayList<PVector> adj = getAdjacent(e.x, e.y);
        Collections.shuffle(adj);
        for (PVector p : adj) {
            if (grid[(int) p.y][(int) p.x].entity == null) {
                grid[e.y][e.x].entity = null;
                e.x = (int) p.x;
                e.y = (int) p.y;
                grid[e.y][e.x].entity = e;

                return;
            }
        }
    }

    void eatGrass(Entity s) {
        Cell c = grid[s.y][s.x];
        if (c.hasGrass) {
            c.hasGrass = false;

        }
    }

    void eatSheep(Entity h) {

        Cell c = grid[h.y][h.x];
        if (c.entity != null && c.entity != h && c.entity.type == EntityType.SHEEP) {
            removeEntity(c.entity, sheeps);

        }
    }

    boolean huntSheep(Entity h) {
        ArrayList<PVector> adj = getAdjacent(h.x, h.y);
        for (PVector p : adj) {
            Cell target = grid[(int) p.y][(int) p.x];
            if (target.hasSheep()) {
                removeEntity(target.entity, sheeps);
                moveEntity(h, target);
                h.eat();
                reproduceHuman(h, humans);
                return true;
            }
        }
        return false;
    }

    void wanderSheep(Entity s) {
        ArrayList<PVector> adj = getAdjacent(s.x, s.y);
        Collections.shuffle(adj);
        for (int i = 0; i < adj.size(); i++) {
            PVector p = adj.get(i);
            Cell target = getCell(p);
            if (target.noEntity() && target.noTree()) {
                if (target.hasGrass) {
                    target.clearGrass();
                    s.eat();
                }
                moveSheep(s, target);
                reproduceSheep(s, sheeps);
                i = adj.size();
            }


        }
    }

    void updateHumans() {
        for (int i = 0; i < humans.size(); i++) {
            Entity s = humans.get(i);
            s.age++;

            if (s.tooOld()) {
                Cell cell1 = getCell(s.x, s.y);
                cell1.clearEntity();
                humans.remove(i);


            } else if (!huntSheep(s)) {
                wanderHuman(s);
            }

        }

    }

    void wanderHuman(Entity h) {
        ArrayList<PVector> adj = getAdjacent(h.x, h.y);
        Collections.shuffle(adj);
        for (int i = 0; i < adj.size(); i++) {
            PVector p = adj.get(i);
            Cell target = getCell(p);
            if (target.hasNoHuman()) {
                moveHuman(h, target);
                if (target.hasTree()) {
                    target.clearTree();
                }
                return;
            }
        }
    }


    void moveHuman(Entity entity, Cell cell) {
        moveEntity(entity, cell);
    }

    void reproduceHuman(Entity parent, ArrayList<Entity> born) {
        if (parent.isCanReproduce()) {
            parent.clearFood();
            ArrayList<PVector> adj = getAdjacent(parent.x, parent.y);

            for (int i = 0; i < adj.size(); i++) {
                PVector p = adj.get(i);
                Cell target = getCell(p);
                if (target.hasNoHuman()) {
                    Entity child = new Entity(EntityType.HUMAN, (int) p.x, (int) p.y);
                    born.add(child);
                    setEntity(child);
                    i = adj.size();
                }

            }
        }


    }

    Cell getCell(int x, int y) {
        return grid[y][x];
    }

    Cell getCell(PVector pVector) {
        return getCell((int) pVector.x, (int) pVector.y);
    }

    void moveEntity(Entity entity, Cell cell) {
        grid[entity.y][entity.x].entity = null;
        entity.x = cell.x;
        entity.y = cell.y;
        grid[cell.y][cell.x].entity = entity;
    }

    void moveSheep(Entity entity, Cell cell) {
        moveEntity(entity, cell);
        cell.clearGrass();
    }

    void setEntity(Entity entity) {
        grid[entity.y][entity.x].entity = null;
        grid[entity.y][entity.x].entity = entity;
    }

    void reproduceSheep(Entity parent, ArrayList<Entity> born) {
        if (parent.isCanReproduce()) {
            parent.clearFood();
            ArrayList<PVector> adj = getAdjacent(parent.x, parent.y);

            for (int i = 0; i < adj.size(); i++) {
                PVector p = adj.get(i);
                Cell target = getCell(p);
                if (target.noEntity() && target.noTree()) {
                    Entity child = new Entity(EntityType.SHEEP, (int) p.x, (int) p.y);
                    born.add(child);
                    setEntity(child);
                    target.clearGrass();
                    i = adj.size();
                }

            }
        }


    }

    void maybeReproduce(Entity parent, ArrayList<Entity> born) {
        if (parent.isCanReproduce()) {
            ArrayList<PVector> adj = getAdjacent(parent.x, parent.y);
            for (PVector p : adj) {
                Cell c = grid[(int) p.y][(int) p.x];
                if (c.entity != null && c.entity.type == parent.type && c.entity.age > 5 && parent.age > 5) {
                    Entity child = new Entity(parent.type, parent.x, parent.y);
                    child.age = 0;
                    born.add(child);


                }
            }
        }

    }

    void placeNewEntity(Entity e) {
        ArrayList<PVector> adj = getAdjacent(e.x, e.y);
        Collections.shuffle(adj);
        for (PVector p : adj) {
            if (grid[(int) p.y][(int) p.x].entity == null) {
                e.x = (int) p.x;
                e.y = (int) p.y;
                grid[e.y][e.x].entity = e;
                if (e.type == EntityType.SHEEP) sheeps.add(e);
                else humans.add(e);
                return;
            }
        }
    }

    void removeEntity(Entity e, ArrayList<Entity> list) {
        grid[e.y][e.x].entity = null;
        list.remove(e);
    }

    ArrayList<PVector> getAdjacent(int x, int y) {
        ArrayList<PVector> list = new ArrayList<>();
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        for (int i = 0; i < dx.length; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < GRID_SIZE_W && ny >= 0 && ny < GRID_SIZE_H) {
                list.add(new PVector(nx, ny));
            }
        }
        return list;
    }

    ArrayList<PVector> getSurrounding(int x, int y) {
        ArrayList<PVector> list = new ArrayList<>();
        int[] dx = {1, -1, 0, 0, 1, 1, -1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, 1, -1};
        for (int i = 0; i < dx.length; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < GRID_SIZE_W && ny >= 0 && ny < GRID_SIZE_H) {
                list.add(new PVector(nx, ny));
            }
        }
        return list;
    }

    void drawGrid() {
        pApplet.stroke(40);
        for (int y = 0; y < GRID_SIZE_H; y++) {
            for (int x = 0; x < GRID_SIZE_W; x++) {
                Cell c = grid[y][x];
                int px = x * CELL_SIZE;
                int py = y * CELL_SIZE;
                pApplet.fill(130, 70, 20);
                pApplet.rect(px, py, CELL_SIZE, CELL_SIZE);
                if (c.hasGrass) {
                    pApplet.fill(100, 150, 30);
                    pApplet.rect(px, py, CELL_SIZE, CELL_SIZE);
                }
                if (c.tree != null) {
                    if (c.tree.dead()) {
                        pApplet.fill(200, 150, 50);
                        pApplet.ellipse(px + CELL_SIZE / 2, py + CELL_SIZE / 2, CELL_SIZE, CELL_SIZE);
                    } else {
                        pApplet.fill(200, 150, 50);
                        pApplet.ellipse(px + CELL_SIZE / 2, py + CELL_SIZE / 2, CELL_SIZE * .5f, CELL_SIZE * .5f);
                    }

                }
                if (c.entity != null) {
                    if (c.entity.type == EntityType.SHEEP) {
                        pApplet.fill(200, 200, 200);
                        //pApplet.ellipseMode(PApplet.CENTER);
                        pApplet.ellipse(px + CELL_SIZE / 2, py + CELL_SIZE / 2, CELL_SIZE * .8f, CELL_SIZE * .4f);
                    } else {
                        pApplet.fill(200, 10, 20);
                        pApplet.ellipse(px + CELL_SIZE / 2, py + CELL_SIZE / 2, CELL_SIZE * .8f, CELL_SIZE * .8f);
                    }
                }

            }
        }
    }


    void drawStats(int x1, int y1, int textsize1) {
        int belowspace = textsize1 * 2;
        int textsidespace = (int) (WIDTH / 2.5f);
        int symbolspace = (int) (textsize1 * .7f);
        int symbolsize = (int) (textsize1 * .5f);
        pApplet.textAlign(pApplet.LEFT, pApplet.TOP);
        pApplet.fill(255);
        pApplet.textSize(textsize1);

        pApplet.fill(200, 10, 20);
        pApplet.ellipse(x1, HEIGHT + y1 + textsize1 / 2, textsize1, textsize1);
        pApplet.fill(255);
        pApplet.text("Humans: " + humans.size(), x1 + symbolsize + symbolspace, HEIGHT + y1);

        int sheepsymbolsizew = textsize1;
        int sheepsymbolsizeh = (int) (sheepsymbolsizew * .8f);
        pApplet.fill(200);
        pApplet.ellipse(x1, HEIGHT + y1 + belowspace, sheepsymbolsizew, sheepsymbolsizeh);
        pApplet.fill(255);
        pApplet.text("Sheep: " + sheeps.size(), x1, HEIGHT + y1 + belowspace);

        pApplet.fill(255);
        pApplet.text("Frame: " + pApplet.frameCount, x1, HEIGHT + y1 + belowspace * 2);

        pApplet.fill(100, 150, 30);
        pApplet.rect(textsidespace, HEIGHT + y1, textsize1, textsize1);
        pApplet.fill(255);
        pApplet.text("Grass Cells: " + countGrass(), textsidespace + symbolsize + symbolspace, HEIGHT + y1);

        pApplet.fill(200, 150, 50);
        pApplet.ellipse(textsidespace, HEIGHT + y1 + belowspace, textsize1, textsize1);
        pApplet.fill(255);
        pApplet.text("Trees: " + countTrees(), textsidespace, HEIGHT + y1 + belowspace);
    }

    int countGrass() {
        int count = 0;
        for (int y = 0; y < GRID_SIZE_H; y++)
            for (int x = 0; x < GRID_SIZE_W; x++)
                if (grid[y][x].hasGrass) count++;
        return count;
    }

    int countTrees() {
        int count = 0;
        for (int y = 0; y < GRID_SIZE_H; y++)
            for (int x = 0; x < GRID_SIZE_W; x++)
                if (grid[y][x].tree != null) count++;
        return count;
    }

    public void draw() {
        pApplet.background(10);
        updateEntities();
        drawGrid();
        drawStatBlock(40, HEIGHT + 50);
    }

    void drawStatBlock(float startX, float startY) {
        float textSizeVal = 34;
        float symbolSize = textSizeVal * 0.8f;
        float lineSpacing = textSizeVal * 1.8f;
        float symbolOffset = symbolSize * 0.5f;
        int textsidespace = (int) (WIDTH / 2.5f);
        pApplet.textSize(textSizeVal);
        pApplet.textAlign(pApplet.LEFT, pApplet.CENTER);

        drawStatusLine(startX, startY + 0 * lineSpacing, symbolSize, "Human: " + humans.size(), pApplet.color(255, 80, 80), "circle");
        drawStatusLine(startX + textsidespace, startY + 0 * lineSpacing, symbolSize, "Sheep: " + sheeps.size(), pApplet.color(200), "oval");
        drawStatusLine(startX, startY + 1 * lineSpacing, symbolSize, "Grass: " + countGrass(), pApplet.color(84, 190, 83), "rect");
        drawStatusLine(startX + textsidespace, startY + 1 * lineSpacing, symbolSize, "Trees: " + countTrees(), pApplet.color(255, 220, 100), "circle");

        pApplet.fill(255);
        pApplet.text("Frame: " + pApplet.frameCount, startX, startY + 2 * lineSpacing);
    }

    void drawStatusLine(float x, float y, float symbolSize, String label, int symbolColor, String shape) {
        pApplet.pushStyle();
        pApplet.fill(symbolColor);
        float symbolX = x + symbolSize * 0.5f;
        float symbolY = y;
        switch (shape) {
            case "circle":
                pApplet.ellipse(symbolX, symbolY, symbolSize, symbolSize);
                break;
            case "oval":
                pApplet.ellipse(symbolX, symbolY, symbolSize * 1.3f, symbolSize * 0.7f);
                break;
            case "rect":
                pApplet.rectMode(pApplet.CENTER);
                pApplet.rect(symbolX, symbolY, symbolSize * 1.2f, symbolSize * 0.6f);
                pApplet.rectMode(pApplet.CORNER);
                break;
        }
        pApplet.fill(255);
        pApplet.text(label, x + symbolSize + 8, y);
        pApplet.popStyle();
    }


}
