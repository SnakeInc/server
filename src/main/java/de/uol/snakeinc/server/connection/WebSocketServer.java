package de.uol.snakeinc.server.connection;

import com.google.gson.Gson;
import de.uol.snakeinc.server.game.Action;
import de.uol.snakeinc.server.game.Game;
import de.uol.snakeinc.server.game.GameHandler;
import de.uol.snakeinc.server.player.Player;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.util.HashMap;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    private GameHandler gameHandler;
    private HashMap<WebSocket, Player> playerHashMap = new HashMap<>();

    public WebSocketServer(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("Got a new client with: " + webSocket.getRemoteSocketAddress());
        String name = webSocket.getResourceDescriptor().toLowerCase();
        Player player = new Player(name, (int)(Math.random() * 40), (int)(Math.random() * 40));
        playerHashMap.put(webSocket, player);
        gameHandler.addPlayer(player);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        playerHashMap.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("Got a new message from: " + webSocket.getRemoteSocketAddress() + " - " + s);
        Gson g = new Gson();
        s = g.fromJson(s, String.class);

        if(s.contains(Action.CHANGE_NOTHING.name())) {
            playerHashMap.get(webSocket).doNothing();
        } else if(s.contains(Action.SLOW_DOWN.name())) {
            playerHashMap.get(webSocket).speedDown();
        } else if(s.contains(Action.SPEED_UP.name())) {
            playerHashMap.get(webSocket).speedUp();
        } else if(s.contains(Action.TURN_LEFT.name())) {
            playerHashMap.get(webSocket).turnLeft();
        } else if(s.contains(Action.TURN_RIGHT.name())) {
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
    }

    @Override
    public void onStart() {
        System.out.println("Server started on port: " + this.getPort());
    }

    public void sendAction(Action action) {
        HashMap<String, String> json = new HashMap<>();
        json.put("action", action.toString().toLowerCase());

        Gson gson = new Gson();

        //this.send(gson.toJson(Json));
    }
}