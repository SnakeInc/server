package de.uol.snakeinc.server.player;

import de.uol.snakeinc.server.game.Game;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Player implements Instructions {

    private final static Logger LOG = Logger.getGlobal();
    private String name;
    private int id;
    private Direction direction = new Direction();
    private int speed = 1;
    private int positionX;
    private int positionY;
    private boolean active = true;
    private boolean ready = false;
    private Game game = null;

    public Player(String name, int startX, int startY) {
        LOG.setLevel(Level.FINEST);
        this.name = name;
        positionX = startX;
        positionY = startY;
    }

    public void turnLeft() {
        direction.turnLeft();
        ready = true;
    }

    public void turnRight() {
        direction.turnRight();
        ready = true;
    }

    public void doNothing() {
        ready = true;
    }

    public void speedUp() {
        ++speed;
        if(speed > 10) {
            active = false;
        }
        ready = true;
    }

    public void speedDown() {
        --speed;
        if(speed < 1) {
            active = false;
        }
        ready = true;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void died() {
        this.active = false;
        //Game ended?
        LOG.fine("Player " + name + " died!");
    }

    public void setReadyFalse() {
        this.ready = false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isActive() {
        return active;
    }

    public String getName() {
        return name;
    }

    public boolean isReady() {
        return ready;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getSpeed() {
        return speed;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }
}
