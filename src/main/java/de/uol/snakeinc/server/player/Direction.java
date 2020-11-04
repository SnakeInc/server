package de.uol.snakeinc.server.player;

public class Direction {

    private int direction = (int)(Math.random() * 4);

    public void turnLeft(){
        if(direction == 0) {
            direction = 3;
        } else {
            --direction;
        }
    }

    public void turnRight(){
        if(direction == 3) {
            direction = 0;
        } else {
            ++direction;
        }
    }

    public int getDirection() {
        return direction;
    }
}
