package de.uol.snakeinc.server.game;

import com.google.inject.Injector;
import de.uol.snakeinc.server.player.Player;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class GameHandler {

    private Injector injector;
    private int gameIdPointer = 0;
    private HashMap<Integer, Game> games = new HashMap<>();

    public void injectInjector(Injector injector) {
        this.injector = injector;
    }

    public Player addPlayer(String name) {
        Player player = new Player(name);
        if(games.isEmpty() || !games.containsKey(gameIdPointer)) {
            Game game = new Game(this, gameIdPointer);
            game.getPlayers().add(player);
            games.put(gameIdPointer, game);
            player.setId(1);
            player.setGame(game);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startGame();
                }
            }, 10000);
        } else {
            games.get(gameIdPointer).getPlayers().add(player);
            player.setId(games.get(gameIdPointer).getPlayers().size() + 1);
            player.setGame(games.get(gameIdPointer));
        }
        return player;
    }

    private void startGame() {
        games.get(gameIdPointer).startGame();
        ++gameIdPointer;
    }

    public void gameEnded(int gameId) {
        games.remove(gameId);
    }

    public Injector getInjector() {
        return injector;
    }

}
