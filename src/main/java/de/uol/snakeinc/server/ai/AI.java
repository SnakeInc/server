package de.uol.snakeinc.server.ai;

import de.uol.snakeinc.server.game.Game;
import de.uol.snakeinc.server.interactor.Interactor;
import de.uol.snakeinc.server.player.Direction;

public class AI extends Interactor {

    private int[][] mapArray = null;
    private int testSpeed;
    private Direction testDirection;

    public AI(String name, Game game) {
        super(name, game);
    }

    public void nextTurn() {
        if(active) {
            mapArray = game.getMap().getMap();
            boolean findTurn = true;
            int countTries = 0;
            while(findTurn) {
                testSpeed = speed;
                try {
                    testDirection = (Direction) direction.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                int r = (int)(Math.random() * 5);
                if (r == 0) {
                    if (!testDoNothing()) {
                        doNothing();
                        findTurn = false;
                    }
                } else if (r == 1) {
                    if (!testTurnLeft()) {
                        turnLeft();
                        findTurn = false;
                    }
                } else if (r == 2) {
                    if (!testTurnRight()) {
                        turnRight();
                        findTurn = false;
                    }
                } else if (r == 3) {
                    if (!testSpeedUp()) {
                        speedUp();
                        findTurn = false;
                    }
                } else if (r == 4) {
                    if (!testSpeedDown()) {
                        speedDown();
                        findTurn = false;
                    }
                }
                ++countTries;
                if(countTries > 20) {
                    doNothing();
                    findTurn = false;
                }
            }
            game.gameReadyNextTurn();
        }
    }

    @Override
    public void died(String reason) {
        if(active) {
            active = false;
            ready = false;
            LOG.fine("Game: " + game.getGameId()+ " ai " + name + " died! Because: " + reason);
        }
    }

    private boolean testDoNothing() {
        return calculateTestFrame();
    }

    private boolean testTurnLeft() {
        testDirection.turnLeft();
        return calculateTestFrame();
    }

    private boolean testTurnRight() {
        testDirection.turnRight();
        return calculateTestFrame();
    }

    private boolean testSpeedUp() {
        ++testSpeed;
        if(testSpeed >= 10) {
            return true;
        }
        return calculateTestFrame();
    }

    private boolean testSpeedDown() {
        --testSpeed;
        if(testSpeed < 1) {
            return true;
        }
        return calculateTestFrame();
    }

    private boolean calculateTestFrame() {
        int playerDirection = testDirection.getDirection();
        int turnCount = game.getMap().getTurnCount();
        if(playerDirection == 1 || playerDirection == 3) {
            int newPositionX;
            if(playerDirection == 1) {
                newPositionX = getPositionX() + testSpeed;
            } else {
                newPositionX = getPositionX() - testSpeed;
            }
            if(newPositionX < 0 || newPositionX >= game.getMap().getxSize()) {
                return true;
            }
            for(int i = 1; i <= testSpeed; ++i) {
                if(turnCount % 6 == 0 && testSpeed > 2) {
                    if(i < 2 || i == testSpeed) {
                        if(playerDirection == 1) {
                            if (testMapEntry(getPositionX() + i, getPositionY())) {
                                return true;
                            }
                        } else {
                            if (testMapEntry(getPositionX() - i, getPositionY())) {
                                return true;
                            }
                        }
                    }
                } else {
                    if(playerDirection == 1) {
                        if (testMapEntry(getPositionX() + i, getPositionY())) {
                            return true;
                        }
                    } else {
                        if (testMapEntry(getPositionX() - i, getPositionY())) {
                            return true;
                        }
                    }
                }
            }
        } else if(playerDirection == 0 || playerDirection == 2) {
            int newPositionY;
            if(playerDirection == 0) {
                newPositionY = getPositionY() - testSpeed;
            } else {
                newPositionY = getPositionY() + testSpeed;
            }
            if(newPositionY < 0 || newPositionY >= game.getMap().getySize()) {
                return true;
            }
            for(int i = 1; i <= testSpeed; ++i) {
                if(turnCount % 6 == 0 && testSpeed > 2) {
                    if(i < 2 || i == testSpeed) {
                        if(playerDirection == 0) {
                            if (testMapEntry(getPositionX(), getPositionY() - i)) {
                                return true;
                            }
                        } else {
                            if (testMapEntry(getPositionX(), getPositionY() + i)) {
                                return true;
                            }
                        }
                    }
                } else {
                    if(playerDirection == 0) {
                        if (testMapEntry(getPositionX(), getPositionY() - i)) {
                            return true;
                        }
                    } else {
                        if (testMapEntry(getPositionX(), getPositionY() + i)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean testMapEntry(int x, int y) {
        if(mapArray != null) {
            if((x >= 0 && x < game.getMap().getxSize()) && (y >= 0 && y < game.getMap().getySize())) {
                if (mapArray[y][x] != 0) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
