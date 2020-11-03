package de.uol.snakeinc.server.game;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import de.uol.snakeinc.server.map.Map;
import de.uol.snakeinc.server.player.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private GameHandler gameHandler;
    private boolean isActive = false;
    private boolean hasStarted = false;
    private int gameId;
    private ArrayList<Player> players = new ArrayList<>();
    private Map map;

    public Game(GameHandler gameHandler, int gameId) {
        this.gameHandler = gameHandler;
        this.gameId = gameId;
    }

    public String nextTurn() {
        int[][] mapIntArray = map.calculateFrame();
        try (JsonWriter writer = new JsonWriter(new FileWriter("C:\\Users\\Jannes\\Desktop\\staff.json"))) {
            writer.beginObject();
            writer.name("width").value(map.getxSize());
            writer.name("height").value(map.getySize());
            writer.name("cells");
            writer.beginArray();
            for (int[] ints : mapIntArray) {
                writer.beginArray();
                for (int anInt : ints) {
                    writer.value(anInt);
                }
                writer.endArray();
            }
            writer.endArray();
            writer.name("players");
            writer.beginObject();
            players.forEach((player) -> {
                try {
                    writer.name(Integer.toString(player.getId()));
                    writer.beginObject();
                    writer.name("x").value(player.getPositionX());
                    writer.name("y").value(player.getPositionY());
                    writer.name("direction");
                    switch (player.getDirection().getDirection()) {
                        case 0:
                            writer.value("up");
                            break;
                        case 1:
                            writer.value("right");
                            break;
                        case 2:
                            writer.value("down");
                            break;
                        case 3:
                            writer.value("left");
                            break;
                    }
                    writer.name("speed").value(player.getSpeed());
                    writer.name("active").value(player.isActive());
                    writer.name("name").value(player.getName());
                    writer.endObject();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.endObject();
            writer.name("you").value(1);
            writer.name("running").value(this.isActive);
            writer.name("deadline").value("2020-11-020T12:00:00Z");
            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "nope";
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isStarted() {
        return hasStarted;
    }

    public void startGame() {
        HashMap<Integer, Player> mapPlayers = new HashMap<>();
        final var ref = new Object() {
            int i = 0;
        };
        this.players.forEach((player) -> {
            mapPlayers.put(ref.i, player);
            ++ref.i;
        });
        map = new Map(40, 40, mapPlayers);
        nextTurn();
        hasStarted = true;
        isActive = true;
        System.out.println("Started game with players: ");
        mapPlayers.forEach((id, player) ->
            System.out.println("ID: " + id + "   Name: " + player.getName())
        );
    }

    public void endGame(int gameId) {
        gameHandler.gameEnded(gameId);
        isActive = false;
    }
}
