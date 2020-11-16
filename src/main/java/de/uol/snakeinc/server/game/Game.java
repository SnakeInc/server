package de.uol.snakeinc.server.game;

import com.google.gson.stream.JsonWriter;
import de.uol.snakeinc.server.connection.ConnectionThread;
import de.uol.snakeinc.server.map.Map;
import de.uol.snakeinc.server.player.Player;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game {

    private final static Logger LOG = Logger.getGlobal();
    private GameHandler gameHandler;
    private boolean isActive = false;
    private boolean hasStarted = false;
    private boolean hasEnded = false;
    private int gameId;
    private ArrayList<Player> players = new ArrayList<>();
    private Map map;
    private Timer gameTimer = new Timer();

    public Game(GameHandler gameHandler, int gameId) {
        this.gameHandler = gameHandler;
        this.gameId = gameId;
        LOG.setLevel(Level.FINEST);
    }

    private void initTurn() {
        int[][] mapIntArray = map.calculateInitFrame();
        generateJsonAndSend(mapIntArray);
    }

    public void nextTurn() {
        int[][] mapIntArray = map.calculateFrame();
        boolean end = false;
        if(players.stream().noneMatch(Player::isActive)) {
            endGame();
            end = true;
        }
        generateJsonAndSend(mapIntArray);
        if(end) {
            gameHandler.getInjector().getInstance(ConnectionThread.class).getWebSocketServer().endGame(this);
        }
    }

    private void generateJsonAndSend(int[][] mapIntArray) {
        StringWriter jsonStringWriter = new StringWriter();
        try (JsonWriter writer = new JsonWriter(jsonStringWriter)) {
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
            writer.name("you").value("");
            writer.name("running").value(this.isActive);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            Instant instant = Instant.ofEpochSecond(Instant.now().getEpochSecond() + 10L);
            String deadline = simpleDateFormat.format(Date.from(instant));
            writer.name("deadline").value(deadline);
            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<Integer, String> playerJsonList = new HashMap<>();
        players.forEach(player -> {
            String playerJson = jsonStringWriter.toString();
            playerJson = playerJson.replaceFirst("(\"you\":\"\")", "\"you\":\"" + player.getId() + "\"");
            playerJsonList.put(player.getId(), playerJson);
        });
        gameHandler.getInjector().getInstance(ConnectionThread.class).getWebSocketServer().sendJson(playerJsonList, this);
        if(!isActive) {
            gameTimer.cancel();
            gameTimer.purge();
        } else {
            resetRoundTimer();
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getGameId() {
        return gameId;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isStarted() {
        return hasStarted;
    }

    public boolean hasEnded() {
        return hasEnded;
    }

    public void startGame() {
        hasStarted = true;
        isActive = true;
        map = new Map((int)(Math.random() * 30) + 31, (int)(Math.random() * 30) + 31, players);
        initTurn();
        LOG.fine("Started game with players: ");
        players.forEach((player) ->
            LOG.fine("ID: " + player.getId() + "   Name: " + player.getName())
        );
    }

    public void endGame() {
        LOG.fine("Game " + gameId + " has ended!");
        isActive = false;
        hasEnded = true;
        gameHandler.gameEnded(gameId);
    }

    public void resetRoundTimer() {
        gameTimer.cancel();
        gameTimer.purge();
        gameTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                LOG.fine("Updated timer");
                nextTurn();
            }
        };
        gameTimer.schedule(timerTask, 14000);
    }
}
