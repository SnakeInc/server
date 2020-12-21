package de.uol.snakeinc.server.connection;

import de.uol.snakeinc.server.game.Action;
import de.uol.snakeinc.server.game.Game;
import de.uol.snakeinc.server.game.GameHandler;
import de.uol.snakeinc.server.interactor.Interactor;
import de.uol.snakeinc.server.player.Player;
import de.uol.snakeinc.server.utility.WebsocketPlayerEntry;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    private GameHandler gameHandler;
    private HashMap<UUID, WebsocketPlayerEntry> playerHashMap = new HashMap<>();
    private final static Logger LOG = Logger.getGlobal();

    public WebSocketServer(GameHandler gameHandler, int port) {
        super(new InetSocketAddress("0.0.0.0", port));
        this.gameHandler = gameHandler;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        LOG.fine("Got a new client with: " + webSocket.getRemoteSocketAddress());
        webSocket.setAttachment(UUID.randomUUID());
        String url = webSocket.getResourceDescriptor().toLowerCase();
        String name;
        if (url.contains("/?key=")) {
            name = url.replace("/?key=", "");
        } else if (url.contains("/")) {
            name = url.replace("/", "");
        } else {
            name = url;
        }
        Player player = gameHandler.addPlayer(name);
        playerHashMap.put(webSocket.getAttachment(), new WebsocketPlayerEntry(player, webSocket));
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        if(playerHashMap.containsKey(webSocket.getAttachment())) {
            Player player = playerHashMap.get(webSocket.getAttachment()).getPlayer();
            player.died("Connection lost!");
            if(player.getGame().getInteractors().contains(player)) {
                player.getGame().getInteractors().remove(player);
            }
            if(player.getGame().isActive() && player.getGame().getInteractors().stream().noneMatch(Interactor::isActive)) {
                player.getGame().endGame();
            }
            playerHashMap.remove(webSocket.getAttachment());
        }
        if(webSocket.isOpen()) {
            webSocket.close();
        }

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        if(playerHashMap.containsKey(webSocket.getAttachment()) && playerHashMap.get(webSocket.getAttachment()).getPlayer().isActive()) {
            LOG.fine("Got a new message from: " + webSocket.getRemoteSocketAddress() + " - Player: " + playerHashMap.get(webSocket.getAttachment()).getPlayer().getName() + " - " + s);
            if(s.contains(Action.CHANGE_NOTHING.name().toLowerCase())) {
                playerHashMap.get(webSocket.getAttachment()).getPlayer().doNothing();
            } else if(s.contains(Action.SLOW_DOWN.name().toLowerCase())) {
                playerHashMap.get(webSocket.getAttachment()).getPlayer().speedDown();
            } else if(s.contains(Action.SPEED_UP.name().toLowerCase())) {
                playerHashMap.get(webSocket.getAttachment()).getPlayer().speedUp();
            } else if(s.contains(Action.TURN_LEFT.name().toLowerCase())) {
                playerHashMap.get(webSocket.getAttachment()).getPlayer().turnLeft();
            } else if(s.contains(Action.TURN_RIGHT.name().toLowerCase())) {
                playerHashMap.get(webSocket.getAttachment()).getPlayer().turnRight();
            }
            playerHashMap.get(webSocket.getAttachment()).getPlayer().getGame().gameReadyNextTurn();
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
        if(!webSocket.isOpen()) {
            playerHashMap.remove(webSocket.getAttachment());
        }
    }

    @Override
    public void onStart() {
        LOG.fine("Server started on port: " + this.getPort());
    }

    public void sendJson(HashMap<Integer, String> playerList, Game game) {
        playerHashMap.forEach(((uuid, wpe) -> {
            Player wpePlayer = playerHashMap.get(uuid).getPlayer();
            WebSocket wpeWebSocket = playerHashMap.get(uuid).getWebSocket();
            if(game == wpe.getPlayer().getGame()) {
                if(playerHashMap.containsKey(uuid) && wpeWebSocket.isOpen()) {
                    wpeWebSocket.send(playerList.get(wpePlayer.getId()));
                }
            }
        }));
    }

    public void endGame(Game game) {
        game.getInteractors().forEach((player) -> playerHashMap.forEach((uuid , wpe) -> {
            if(player == wpe.getPlayer() && wpe.getWebSocket().isOpen()) {
                wpe.getWebSocket().close();
            }
        }));
    }

}