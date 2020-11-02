package de.uol.snakeinc.server.game;

import de.uol.snakeinc.server.player.Player;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class GameHandler {

    private int gameIdPointer = 0;
    private HashMap<Integer, Game> games = new HashMap<>();

    public Game addPlayer(Player player) {
        if(games.isEmpty() || games.get(gameIdPointer).isStarted()) {
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
        return games.get(gameIdPointer);
    }

    private void startGame() {
        games.get(gameIdPointer).startGame();
        ++gameIdPointer;
    }

    public void gameEnded(int gameId) {
        games.remove(gameId);
    }

}
