package org.francoRobles;


import static org.francoRobles.util.*;

public class Snake {

    private int bodyParts = INIT_BODY_PART;
    int x[] = new int[GAME_UNITS ];
    int y[] = new int[ GAME_UNITS];
    private int applesEatens=0;
    Direction direction = Direction.Right;



    public void resetSnake(){
        this.setX(new int[GAME_UNITS]);
        this.setY(new int[GAME_UNITS]);
        this.setBodyParts(1);
        this.applesEatens = 0;
        this.setDirection(Direction.Right);
    }
    public void move(){
        for (int i = this.getBodyParts(); i > 0; i--) {
            this.x[i] = this.x[i-1];
            this.y[i] = this.y[i-1];
        }
        switch (this.getDirection()){
            case UP -> this.y[0] = this.y[0]-UNIT_SIZE;
            case Down -> this.y[0] = this.y[0]+UNIT_SIZE;
            case Left -> this.x[0] = this.x[0]-UNIT_SIZE;
            case Right -> this.x[0]= this.x[0]+UNIT_SIZE;
        }

    }
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getApplesEatens() {
        return applesEatens;
    }

    public void setApplesEatens(int applesEatens) {
        this.applesEatens = applesEatens;
    }

    public void addEatensApple() {
        this.applesEatens += 1;
    }

    public void addBodyPart() {
        this.bodyParts += 1;
    }

    public int getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(int bodyParts) {
        this.bodyParts = bodyParts;
    }

    public int[] getX() {
        return x;
    }

    public void setX(int[] x) {
        this.x = x;
    }

    public int[] getY() {
        return y;
    }

    public void setY(int[] y) {
        this.y = y;
    }
}
