package de.uol.snakeinc.server.connection;

import com.google.inject.Injector;
import de.uol.snakeinc.server.game.GameHandler;
import de.uol.snakeinc.server.server.WebSocketEnableWss;

import java.io.IOException;

public class ConnectionThread extends Thread {

        private WebSocketServer webSocketServer;
        private Injector injector;
        private boolean running;

        public ConnectionThread() {
                this.running = true;
        }

        public void injectInjector(Injector injector) {
                this.injector = injector;
        }

        @Override
        public void run() {
                while (running) {
                        webSocketServer = new WebSocketEnableWss().initWebSocketServer(injector.getInstance(GameHandler.class), 555);
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

        public WebSocketServer getWebSocketServer() {
                return webSocketServer;
        }
}
