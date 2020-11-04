package de.uol.snakeinc.server.map;

import de.uol.snakeinc.server.player.Player;

import java.util.HashMap;

public class Map {

    private int xSize;
    private int ySize;
    private int turnCount = 1;
    private HashMap<Integer, Player> players;
    private int[][] oldMap;

    public Map(int xSize, int ySize, HashMap<Integer, Player> players) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.players = players;
        this.oldMap = new int[ySize][xSize];
    }

    public int[][] calculateInitFrame() {
        players.forEach((id, player) -> setMapEntry(player.getPositionX(), player.getPositionY(), player.getId()));
        return oldMap;
    }

    public int[][] calculateFrame() {
        players.forEach((id, player) -> {
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
                            player.died();
                        }
                        for(int i = 1; i <= playerSpeed; ++i) {
                            if(turnCount % 6 == 0 && playerSpeed > 2) {
                                if(i < 2 || i == playerSpeed) {
                                    if(playerDirection == 1) {
                                        setMapEntry(player.getPositionX() + i, player.getPositionY(), player.getId());
                                    } else {
                                        setMapEntry(player.getPositionX() - i, player.getPositionY(), player.getId());
                                    }
                                }
                            } else {
                                if(playerDirection == 1) {
                                    setMapEntry(player.getPositionX() + i, player.getPositionY(), player.getId());
                                } else {
                                    setMapEntry(player.getPositionX() - i, player.getPositionY(), player.getId());
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
                            player.died();
                        }
                        for(int i = 1; i <= playerSpeed; ++i) {
                            if(turnCount % 6 == 0 && playerSpeed > 2) {
                                if(i < 2 || i == playerSpeed) {
                                    if(playerDirection == 0) {
                                        setMapEntry(player.getPositionX(), player.getPositionY() - i, player.getId());
                                    } else {
                                        setMapEntry(player.getPositionX(), player.getPositionY() + i, player.getId());
                                    }
                                }
                            } else {
                                if(playerDirection == 0) {
                                    setMapEntry(player.getPositionX(), player.getPositionY() - i, player.getId());
                                } else {
                                    setMapEntry(player.getPositionX(), player.getPositionY() + i, player.getId());
                                }
                            }
                        }
                        player.setPositionY(newPositionY);
                    }
                    player.setReadyFalse();
                } else {
                    player.died();
                }
            }
        });
        ++turnCount;
        return oldMap;
    }

    private void setMapEntry(int x, int y, Integer playerId) {
        if((x >= 0 && x < xSize) && (y >= 0 && y < ySize)) {
            if (oldMap[y][x] != 0) {
                oldMap[y][x] = -1;
                players.get(playerId).died();
            } else {
                oldMap[y][x] = playerId;
            }
        }
    }

    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }
}
