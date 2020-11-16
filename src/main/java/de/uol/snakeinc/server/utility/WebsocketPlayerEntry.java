package de.uol.snakeinc.server.utility;

import de.uol.snakeinc.server.player.Player;
import org.java_websocket.WebSocket;

public class WebsocketPlayerEntry {

    private Player player;
    private WebSocket webSocket;

    public WebsocketPlayerEntry(Player player, WebSocket webSocket) {
        this.player = player;
        this.webSocket = webSocket;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }
}
