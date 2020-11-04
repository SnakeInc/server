package de.uol.snakeinc.server.connection;

import de.uol.snakeinc.server.game.Action;
import de.uol.snakeinc.server.game.Game;
import de.uol.snakeinc.server.game.GameHandler;
import de.uol.snakeinc.server.player.Player;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    private GameHandler gameHandler;
    private HashMap<WebSocket, Player> playerHashMap = new HashMap<>();
    private final static Logger LOG = Logger.getGlobal();

    public WebSocketServer(GameHandler gameHandler, int port) {
        super(new InetSocketAddress("0.0.0.0", port));
        this.gameHandler = gameHandler;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        LOG.fine("Got a new client with: " + webSocket.getRemoteSocketAddress());
        String name = webSocket.getResourceDescriptor().toLowerCase().replace("/?key=", "");
        Player player = new Player(name, (int)(Math.random() * 40), (int)(Math.random() * 40));
        playerHashMap.put(webSocket, player);
        gameHandler.addPlayer(player);
        player.getGame().getPlayers().forEach((playerEntry) -> {
            if((player != playerEntry)
                && (player.getPositionX() == playerEntry.getPositionX()
                && player.getPositionY() == playerEntry.getPositionY())) {
                player.setPositionX(playerEntry.getPositionX() + 2);
                player.setPositionY(playerEntry.getPositionY() + 2);
            }
        });
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        webSocket.close();
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        LOG.fine("Got a new message from: " + webSocket.getRemoteSocketAddress() + " - " + s);
        if(s.contains(Action.CHANGE_NOTHING.name().toLowerCase())) {
            playerHashMap.get(webSocket).doNothing();
        } else if(s.contains(Action.SLOW_DOWN.name().toLowerCase())) {
            playerHashMap.get(webSocket).speedDown();
        } else if(s.contains(Action.SPEED_UP.name().toLowerCase())) {
            playerHashMap.get(webSocket).speedUp();
        } else if(s.contains(Action.TURN_LEFT.name().toLowerCase())) {
            playerHashMap.get(webSocket).turnLeft();
        } else if(s.contains(Action.TURN_RIGHT.name().toLowerCase())) {
            playerHashMap.get(webSocket).turnRight();
        }
        Game game = playerHashMap.get(webSocket).getGame();
        int countReadyPlayers = (int) game.getPlayers().stream().filter(Player::isReady).count();
        if(countReadyPlayers == game.getPlayers().size()) {
            game.nextTurn();
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
        playerHashMap.remove(webSocket);
    }

    @Override
    public void onStart() {
        LOG.fine("Server started on port: " + this.getPort());
    }

    public void sendJson(HashMap<Integer, String> playerList, Game game) {
        playerHashMap.forEach(((webSocket, player) -> {
            if(game == player.getGame()) {
                webSocket.send(playerList.get(player.getId()));
            }
        }));
    }

    public void endGame(Game game) {
        game.getPlayers().forEach((player) -> {
            if(playerHashMap.containsValue(player)) {
                playerHashMap.forEach((websocket, playerHM) -> {
                    if(playerHM == player) {
                        websocket.close();
                    }
                });
            }
        });
    }

}