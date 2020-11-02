package de.uol.snakeinc.server.connection;

import de.uol.snakeinc.server.game.GameHandler;

import java.io.IOException;

public class ConnectionThread extends Thread {

        private WebSocketServer webSocketServer;
        private GameHandler gameHandler;
        private boolean running;

        public ConnectionThread(GameHandler gameHandler) {
                this.gameHandler = gameHandler;
                this.running = true;
        }

        @Override
        public void run() {
                while (running) {
                        webSocketServer = new WebSocketServer(gameHandler);
                        webSocketServer.run();
                }
        }

        public void stopServer() {
                this.running = false;
                try {
                        this.webSocketServer.stop();
                } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                }
        }
}
