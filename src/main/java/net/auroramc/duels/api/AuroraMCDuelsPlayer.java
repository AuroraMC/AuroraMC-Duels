package net.auroramc.duels.api;

import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.duels.api.game.Game;

public class AuroraMCDuelsPlayer extends AuroraMCPlayer {

    private Game game;

    public AuroraMCDuelsPlayer(AuroraMCPlayer oldPlayer) {
        super(oldPlayer);
        this.game = null;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isInGame() {
        return game != null;
    }
}
