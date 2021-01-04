package de.uol.snakeinc.server.export;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uol.snakeinc.server.game.Game;
import de.uol.snakeinc.server.interactor.Interactor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Export {

    private final static Logger LOG = Logger.getGlobal();
    private ExportManager manager;
    private Game game;
    private final String usName = "SnakeInc";

    public Export(ExportManager manager, Game game) {
        this.manager = manager;
        this.game = game;
    }

    /**
     * Generate Javadoc-File.
     */
    public void generateFile() {
        LOG.info("Writing log for " + game.getGameId());
        Gson gson = new Gson();
        JsonObject objects = new JsonObject();
        objects.addProperty("rounds", game.getTurnCount());
        objects.addProperty("date", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));
        AtomicInteger us = new AtomicInteger(-1);
        game.getInteractors().stream().filter((interactor) -> interactor.getName().toLowerCase().contains(usName.toLowerCase())).forEach((interactor -> {
            us.set(interactor.getId());
        }));
        objects.addProperty("us", us.get());
        HashMap<Integer, String> players = new HashMap<Integer, String>();
        for (Interactor interactor : game.getInteractors()) {
            players.put(interactor.getId(), interactor.getName());
        }
        objects.add("players", gson.toJsonTree(players));

        HashMap<Integer, int[][]> boards = game.exportMapHistory();
        JsonObject mapObjects = new JsonObject();
        JsonElement jsonElement = gson.toJsonTree(boards);
        mapObjects.addProperty("width", game.getXSize());
        mapObjects.addProperty("height", game.getYSize());
        mapObjects.add("boards", jsonElement);
        objects.add("map", mapObjects);

        String json = gson.toJson(objects);

        try {
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            Date date = new Date(System.currentTimeMillis());
            File file = new File("logs_554", formatter.format(date) + ".json");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException exception) {
            LOG.warning("An error occurred while saving the file.");
            exception.printStackTrace();
        }
    }

}