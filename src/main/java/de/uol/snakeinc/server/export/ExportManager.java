package de.uol.snakeinc.server.export;

import de.uol.snakeinc.server.game.Game;

public class ExportManager {

    /**
     * Generate Export for a Game.
     * @param game game to export
     */
    public void generateExport(Game game) {
        Export export = new Export(this, game);
        export.generateFile();
    }

}