package de.uol.snakeinc.server.player;

import de.uol.snakeinc.server.game.Game;
import de.uol.snakeinc.server.interactor.Interactor;

public class Player extends Interactor {


    public Player(String name, Game game) {
        super(name, game);
    }

    @Override
    public void died(String reason) {
        if(active) {
            active = false;
            LOG.fine("Game: " + game.getGameId()+ " player " + name + " died! Because: " + reason);
        }
    }
}
