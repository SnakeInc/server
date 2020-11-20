package de.uol.snakeinc.server.ai;

import de.uol.snakeinc.server.game.Action;
import de.uol.snakeinc.server.player.Direction;

import java.util.HashMap;

public class AIPosition {

    public int x;
    private int oldX;
    public int y;
    private int oldY;
    public int speed;
    private int oldSpeed;
    public Direction direction;
    private Direction oldDirection;
    private int currentDepth = 0;
    private HashMap<Integer, Object[]> history = new HashMap<>();
    private int[][] mapArray;

    public AIPosition(int x, int y, int speed, Direction direction, int[][] mapArray) {
        this.oldX = x;
        this.oldY = y;
        this.oldSpeed = speed;
        this.oldDirection = direction;
        this.mapArray = mapArray;
    }

    public void init() throws CloneNotSupportedException {
        x = oldX;
        y = oldY;
        speed = oldSpeed;
        direction = oldDirection.clone();
        currentDepth = 0;
        history.clear();
    }

    public void resetToDepth(int depth) {
        currentDepth = depth;
        if(history.containsKey(depth)) {
            Object[] data = history.get(depth);
            x = (int)data[0];
            y = (int)data[1];
            speed = (int)data[2];
            direction = (Direction)data[3];
            int i = depth;
            while (history.containsKey(i + 1)) {
                history.remove(i + 1);
                i++;
            }
        }
    }

    public boolean calculateNewPosition(Action action) throws CloneNotSupportedException {
        if (action.equals(Action.TURN_LEFT)) {
            direction.turnLeft();
        } else if (action.equals(Action.TURN_RIGHT)) {
            direction.turnRight();
        } else if (action.equals(Action.SPEED_UP)) {
            speed++;
            if(speed >= 10) {
                return false;
            }
        } else if (action.equals(Action.SLOW_DOWN)) {
            speed--;
            if(speed <= 0) {
                return false;
            }
        }
        ++currentDepth;
        if(calculateTestFrame()) {
            history.put(currentDepth, new Object[]{x, y, speed, direction.clone()});
            return true;
        } else {
            return false;
        }
    }

    private boolean calculateTestFrame() {
        int playerDirection = direction.getDirection();
        if(playerDirection == 1 || playerDirection == 3) {
            int newPositionX;
            if(playerDirection == 1) {
                newPositionX = x + speed;
            } else {
                newPositionX = x - speed;
            }
            if(newPositionX < 0 || newPositionX >= mapArray[0].length) {
                return false;
            }
            for(int i = 1; i <= speed; ++i) {
                if(playerDirection == 1) {
                    if (testMapEntry(x + i, y)) {
                        return false;
                    }
                } else {
                    if (testMapEntry(x - i, y)) {
                        return false;
                    }
                }
            }
            x = newPositionX;
        } else if(playerDirection == 0 || playerDirection == 2) {
            int newPositionY;
            if(playerDirection == 0) {
                newPositionY = y - speed;
            } else {
                newPositionY = y + speed;
            }
            if(newPositionY < 0 || newPositionY >= mapArray.length) {
                return false;
            }
            for(int i = 1; i <= speed; ++i) {
                if(playerDirection == 0) {
                    if (testMapEntry(x, y - i)) {
                        return false;
                    }
                } else {
                    if (testMapEntry(x, y + i)) {
                        return false;
                    }
                }
            }
            y = newPositionY;
        }
        return true;
    }

    private boolean testMapEntry(int x, int y) {
        if(mapArray != null) {
            if((x >= 0 && x < mapArray[0].length) && (y >= 0 && y < mapArray.length)) {
                return mapArray[y][x] != 0;
            }
        }
        return true;
    }
}
