package de.uol.snakeinc.server;

import de.uol.snakeinc.server.connection.ConnectionThread;
import de.uol.snakeinc.server.game.GameHandler;

public class Server {
    public static void main(String[] args) {
        GameHandler gameHandler = new GameHandler();
        ConnectionThread connectionThread = new ConnectionThread(gameHandler);
        connectionThread.start();

    }
}
