package de.uol.snakeinc.server.game;

import com.google.gson.Gson;
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
        Gson g = new Gson();
        try {
            g.toJson(map, new FileWriter("C:\\Users\\Jannes\\Desktop\\staff.json"));
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
