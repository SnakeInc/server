package de.uol.snakeinc.server.interactor;

import de.uol.snakeinc.server.game.Game;
import de.uol.snakeinc.server.player.Direction;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Interactor implements Instructions {

    public final static Logger LOG = Logger.getGlobal();
    public String name;
    public int id;
    public Direction direction = new Direction();
    public int speed = 1;
    public int positionX = 0;
    public int positionY = 0;
    public boolean active = true;
    public boolean ready = false;
    public Game game = null;

    public Interactor(String name, Game game) {
        LOG.setLevel(Level.FINEST);
        this.name = name;
        this.game = game;
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
        if(speed >= 10) {
            died("Too fast!");
        }
        ready = true;
    }

    public void speedDown() {
        --speed;
        if(speed < 1) {
            died("Too slow!");
        }
        ready = true;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    abstract public void died(String reason);

    public void setReadyFalse() {
        this.ready = false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isNotActive() {
        return !active;
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
