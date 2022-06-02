package net.auroramc.duels.api;

import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.duels.api.game.DuelInvite;
import net.auroramc.duels.api.game.Game;
import net.auroramc.duels.api.game.GameRewards;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuroraMCDuelsPlayer extends AuroraMCPlayer {

    private Game game;
    private AuroraMCDuelsPlayer lastHitBy;
    private long lastHitAt;

    private final Map<AuroraMCDuelsPlayer, Long> latestHits;
    private GameRewards rewards;

    private final Map<UUID, DuelInvite> pendingIncomingInvites;
    private DuelInvite pendingOutgoingInvite;

    public AuroraMCDuelsPlayer(AuroraMCPlayer oldPlayer) {
        super(oldPlayer);
        this.game = null;
        latestHits = new HashMap<>();
        pendingIncomingInvites = new HashMap<>();
        pendingOutgoingInvite = null;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
        if (game != null) {
           rewards = new GameRewards(this);
        }
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

    public void gameOver() {
        rewards = null;
    }

    public void newOutgoing(DuelInvite invite) {
        pendingOutgoingInvite = invite;
    }

    public void removeOutgoing() {
        pendingOutgoingInvite = null;
    }

    public DuelInvite getPendingOutgoingInvite() {
        return pendingOutgoingInvite;
    }

    public Map<UUID, DuelInvite> getPendingIncomingInvites() {
        return pendingIncomingInvites;
    }

    public void newIncoming(DuelInvite invite) {
        pendingIncomingInvites.put(invite.getInviter().getPlayer().getUniqueId(), invite);
    }

    public void removeIncoming(DuelInvite invite) {
        pendingIncomingInvites.remove(invite.getInviter().getPlayer().getUniqueId());
    }
}
