package de.uol.snakeinc.server.map;

import de.uol.snakeinc.server.player.Player;

import java.util.HashMap;

public class Map {

    private int xSize;
    private int ySize;
    private int playerCount;
    private int turnCount = 1;
    private HashMap<Integer, Player> players;
    private int[][] oldMap;

    public Map(int xSize, int ySize, HashMap<Integer, Player> players) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.players = players;
        this.oldMap = new int[xSize][ySize];
        this.playerCount = players.size();
    }

    public int[][] calculateFrame() {
        players.forEach((id, player) -> {
            if(player.isActive()) {
                if(player.isReady()) {
                    int playerDirection = player.getDirection().getDirection();
                    int playerSpeed = player.getSpeed();
                    if(playerDirection == 0 || playerDirection == 2) {
                        int newPositionX;
                        if(playerDirection == 0) {
                            newPositionX = player.getPositionX() - playerSpeed;
                        } else {
                            newPositionX = player.getPositionX() + playerSpeed;
                        }
                        if(newPositionX < 0 || newPositionX > xSize) {
                            player.setActive(false);
                        }
                        for(int i = 0; i < playerSpeed; ++i) {
                            if(turnCount % 6 == 0 && playerSpeed > 2) {
                                if(i < 2 || i == playerSpeed - 1) {
                                    if(playerDirection == 0) {
                                        setMapEntry(player.getPositionX() - i, player.getPositionY(), player.getId());
                                    } else {
                                        setMapEntry(player.getPositionX() + i, player.getPositionY(), player.getId());
                                    }
                                }
                            } else {
                                if(playerDirection == 0) {
                                    setMapEntry(player.getPositionX() - i, player.getPositionY(), player.getId());
                                } else {
                                    setMapEntry(player.getPositionX() + i, player.getPositionY(), player.getId());
                                }
                            }
                        }
                        player.setPositionX(newPositionX);
                    } else if(playerDirection == 1 || playerDirection == 3) {
                        int newPositionY;
                        if(playerDirection == 1) {
                            newPositionY = player.getPositionY() + playerSpeed;
                        } else {
                            newPositionY = player.getPositionY() - playerSpeed;
                        }
                        if(newPositionY < 0 || newPositionY > ySize) {
                            player.setActive(false);
                        }
                        for(int i = 0; i < playerSpeed; ++i) {
                            if(turnCount % 6 == 0 && playerSpeed > 2) {
                                if(i < 2 || i == playerSpeed - 1) {
                                    if(playerDirection == 1) {
                                        setMapEntry(player.getPositionX(), player.getPositionY() + i, player.getId());
                                    } else {
                                        setMapEntry(player.getPositionX(), player.getPositionY() - i, player.getId());
                                    }
                                }
                            } else {
                                if(playerDirection == 1) {
                                    setMapEntry(player.getPositionX(), player.getPositionY() + i, player.getId());
                                } else {
                                    setMapEntry(player.getPositionX(), player.getPositionY() - i, player.getId());
                                }
                            }
                        }
                        player.setPositionY(newPositionY);
                    }
                } else {
                    player.setActive(false);
                }
            }
        });
        ++turnCount;
        return oldMap;
    }

    private void setMapEntry(int x, int y, int playerId) {
        if (oldMap[x][y] != 0) {
            oldMap[x][y] = -1;
            players.get(playerId).setActive(false);
        } else {
            oldMap[x][y] = playerId;
        }
    }

    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }
}
