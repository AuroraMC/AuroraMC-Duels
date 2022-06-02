package net.auroramc.duels.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.communication.CommunicationUtils;
import net.auroramc.core.api.backend.communication.Protocol;
import net.auroramc.core.api.backend.communication.ProtocolMessage;
import net.auroramc.core.api.events.ProtocolMessageEvent;
import net.auroramc.core.api.events.ServerCloseRequestEvent;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.backend.DuelsDatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ShutdownRequestListener implements Listener {

    @EventHandler
    public void onServerCloseRequest(ServerCloseRequestEvent e) {
        if (e.isEmergency() || DuelsAPI.getGames().size() == 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Server Manager", "This server is restarting" + ((!e.isEmergency())?" for an update":"") + ". You are being sent to a lobby."));
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Lobby");
                out.writeUTF(player.getUniqueId().toString());
                player.sendPluginMessage(AuroraMCAPI.getCore(), "BungeeCord", out.toByteArray());
            }
            //Wait 10 seconds, then close the server
            new BukkitRunnable(){
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.kickPlayer(AuroraMCAPI.getFormatter().pluginMessage("Server Manager", "This server is restarting.\n\nYou can reconnect to the network to continue playing!"));
                    }
                    AuroraMCAPI.setShuttingDown(true);
                    CommunicationUtils.sendMessage(new ProtocolMessage(Protocol.CONFIRM_SHUTDOWN, "Mission Control", e.getType(), AuroraMCAPI.getServerInfo().getName(), AuroraMCAPI.getServerInfo().getNetwork().name()));
                }
            }.runTaskLater(AuroraMCAPI.getCore(), 200);
        } else {
            //Set that it is awaiting a restart, then restart when the game is over.
            DuelsAPI.setAwaitingRestart(true);
            DuelsAPI.setRestartType(e.getType());
        }
    }

    @EventHandler
    public void onProtocolMessage(ProtocolMessageEvent e) {
        if (e.getMessage().getProtocol() == Protocol.UPDATE_PLAYER_COUNT) {
            DuelsAPI.setXpBoostMessage(DuelsDatabaseManager.getXpMessage());
            DuelsAPI.setXpBoostMultiplier(DuelsDatabaseManager.getXpMultiplier());
        } else if (e.getMessage().getProtocol() == Protocol.UPDATE_MAPS) {
            if (DuelsAPI.getGames().size() == 0) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Server Manager", "This server is restarting for an update. You are being sent to a lobby."));
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Lobby");
                    out.writeUTF(player.getUniqueId().toString());
                    player.sendPluginMessage(AuroraMCAPI.getCore(), "BungeeCord", out.toByteArray());
                }
                //Wait 10 seconds, then close the server
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.kickPlayer(AuroraMCAPI.getFormatter().pluginMessage("Server Manager", "This server is restarting.\n\nYou can reconnect to the network to continue playing!"));
                        }
                        AuroraMCAPI.setShuttingDown(true);
                        CommunicationUtils.sendMessage(new ProtocolMessage(Protocol.CONFIRM_SHUTDOWN, "Mission Control", "restart", AuroraMCAPI.getServerInfo().getName(), AuroraMCAPI.getServerInfo().getNetwork().name()));
                    }
                }.runTaskLater(AuroraMCAPI.getCore(), 200);
            } else {
                //Set that it is awaiting a restart, then restart when the game is over.
                DuelsAPI.setAwaitingRestart(true);
                DuelsAPI.setRestartType("restart");
            }
        }
    }

}
