package de.uol.snakeinc.server.map;

import de.uol.snakeinc.server.player.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Map {

    private int xSize;
    private int ySize;
    private int turnCount = 1;
    private ArrayList<Player> players;
    private int[][] map;
    private HashMap<int[], Player> logRounds = new HashMap<>();

    public Map(int xSize, int ySize, ArrayList<Player> players) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.players = players;
        this.map = new int[ySize][xSize];
    }

    public int[][] calculateInitFrame() {
        players.forEach((listPlayer) -> {
            listPlayer.setPositionX((int)(Math.random() * xSize));
            listPlayer.setPositionY((int)(Math.random() * ySize));
            setMapEntry(listPlayer.getPositionX(), listPlayer.getPositionY(), listPlayer);
        });
        return map;
    }

    public int[][] calculateFrame() {
        players.forEach((player) -> {
            if(player.isActive()) {
                if(player.isReady()) {
                    int playerDirection = player.getDirection().getDirection();
                    int playerSpeed = player.getSpeed();
                    if(playerDirection == 1 || playerDirection == 3) {
                        int newPositionX;
                        if(playerDirection == 1) {
                            newPositionX = player.getPositionX() + playerSpeed;
                        } else {
                            newPositionX = player.getPositionX() - playerSpeed;
                        }
                        if(newPositionX < 0 || newPositionX >= xSize) {
                            player.died("Not in map!");
                        }
                        for(int i = 1; i <= playerSpeed; ++i) {
                            if(turnCount % 6 == 0 && playerSpeed > 2) {
                                if(i < 2 || i == playerSpeed) {
                                    if(playerDirection == 1) {
                                        setMapEntry(player.getPositionX() + i, player.getPositionY(), player);
                                    } else {
                                        setMapEntry(player.getPositionX() - i, player.getPositionY(), player);
                                    }
                                }
                            } else {
                                if(playerDirection == 1) {
                                    setMapEntry(player.getPositionX() + i, player.getPositionY(), player);
                                } else {
                                    setMapEntry(player.getPositionX() - i, player.getPositionY(), player);
                                }
                            }
                        }
                        player.setPositionX(newPositionX);
                    } else if(playerDirection == 0 || playerDirection == 2) {
                        int newPositionY;
                        if(playerDirection == 0) {
                            newPositionY = player.getPositionY() - playerSpeed;
                        } else {
                            newPositionY = player.getPositionY() + playerSpeed;
                        }
                        if(newPositionY < 0 || newPositionY >= ySize) {
                            player.died("Not in map!");
                        }
                        for(int i = 1; i <= playerSpeed; ++i) {
                            if(turnCount % 6 == 0 && playerSpeed > 2) {
                                if(i < 2 || i == playerSpeed) {
                                    if(playerDirection == 0) {
                                        setMapEntry(player.getPositionX(), player.getPositionY() - i, player);
                                    } else {
                                        setMapEntry(player.getPositionX(), player.getPositionY() + i, player);
                                    }
                                }
                            } else {
                                if(playerDirection == 0) {
                                    setMapEntry(player.getPositionX(), player.getPositionY() - i, player);
                                } else {
                                    setMapEntry(player.getPositionX(), player.getPositionY() + i, player);
                                }
                            }
                        }
                        player.setPositionY(newPositionY);
                    }
                    player.setReadyFalse();
                } else {
                    player.died("Not ready!");
                }
            }
        });
        ++turnCount;
        return map;
    }

    private void setMapEntry(int x, int y, Player player) {
        if((x >= 0 && x < xSize) && (y >= 0 && y < ySize)) {
            if (map[y][x] != 0) {
                map[y][x] = -1;
                player.died("Collision!");
                checkRoundLog(x, y);
            } else {
                map[y][x] = player.getId();
                logOneRound(player, x, y);
            }
        }
    }

    private void logOneRound(Player player, int x, int y) {
        int[] intArray = new int[3];
        intArray[0] = turnCount;
        intArray[1] = x;
        intArray[2] = y;
        logRounds.put(intArray, player);
    }

    private void checkRoundLog(int x, int y) {
        logRounds.forEach((intArray, player) -> {
            if(intArray[0] == turnCount && intArray[1] == x && intArray[2] == y) {
                player.died("Collision!");
            }
        });
    }

    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }
}