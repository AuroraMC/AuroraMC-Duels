package net.auroramc.duels.api;

import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.duels.api.game.Game;
import net.auroramc.duels.api.game.GameRewards;

import java.util.HashMap;
import java.util.Map;

public class AuroraMCDuelsPlayer extends AuroraMCPlayer {

    private Game game;
    private AuroraMCDuelsPlayer lastHitBy;
    private long lastHitAt;

    private final Map<AuroraMCDuelsPlayer, Long> latestHits;
    private GameRewards rewards;

    public AuroraMCDuelsPlayer(AuroraMCPlayer oldPlayer) {
        super(oldPlayer);
        this.game = null;
        latestHits = new HashMap<>();
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

    public AuroraMCDuelsPlayer getLastHitBy() {
        return lastHitBy;
    }

    public long getLastHitAt() {
        return lastHitAt;
    }

    public void setLastHitAt(long lastHitAt) {
        this.lastHitAt = lastHitAt;
    }

    public void setLastHitBy(AuroraMCDuelsPlayer lastHitBy) {
        this.lastHitBy = lastHitBy;
    }

    public Map<AuroraMCDuelsPlayer, Long> getLatestHits() {
        return latestHits;
    }

    public GameRewards getRewards() {
        return rewards;
    }
}
