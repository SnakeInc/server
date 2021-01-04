package de.uol.snakeinc.server.server;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uol.snakeinc.server.connection.ConnectionThread;
import de.uol.snakeinc.server.export.ExportManager;
import de.uol.snakeinc.server.game.GameHandler;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerModule extends AbstractModule {

    private GameHandler gameHandler = new GameHandler();
    private ConnectionThread connectionThread  = new ConnectionThread();
    private ExportManager exportManager = new ExportManager();
    private final Injector injector = Guice.createInjector(this);

    public ServerModule() {
        inject();
        connectionThread.start();
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.ALL);
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    @Override
    protected void configure() {
        bind(GameHandler.class).toInstance(gameHandler);
        bind(ConnectionThread.class).toInstance(connectionThread);
        bind(ServerModule.class).toInstance(this);
        bind(ExportManager.class).toInstance(exportManager);
    }

    private void inject() {
        gameHandler.injectInjector(injector);
        connectionThread.injectInjector(injector);
    }

}
