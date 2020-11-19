package de.uol.snakeinc.server.game;

import com.google.inject.Injector;
import de.uol.snakeinc.server.ai.AI;
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
        Player player;
        if(games.isEmpty() || !games.containsKey(gameIdPointer)) {
            Game game = new Game(this, gameIdPointer);
            player = new Player(name, game);
            game.getInteractors().add(player);
            games.put(gameIdPointer, game);
            player.setId(1);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    addAiToGame(game, 4);
                    startGame();
                }
            }, 10000);
        } else {
            player = new Player(name, games.get(gameIdPointer));
            games.get(gameIdPointer).getInteractors().add(player);
            player.setId(games.get(gameIdPointer).getInteractors().size() + 1);
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

    private void addAiToGame(Game game, int maxPlayers) {
        while (game.getInteractors().size() < maxPlayers) {
            String aiName = "AI_" + (game.getInteractors().size() + 1);
            AI ai = new AI(aiName, game);
            ai.setId(game.getInteractors().size() + 1);
            game.getInteractors().add(ai);
        }
    }

}
