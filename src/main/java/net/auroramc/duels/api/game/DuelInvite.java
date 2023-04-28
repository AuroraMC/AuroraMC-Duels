package net.auroramc.duels.api.game;

import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.DuelsMap;
import net.auroramc.duels.listeners.LobbyListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DuelInvite {

    private AuroraMCDuelsPlayer inviter;
    private AuroraMCDuelsPlayer invited;

    private Kit kit;

    public BukkitTask task;

    public DuelInvite(AuroraMCDuelsPlayer inviter, AuroraMCDuelsPlayer invited, Kit kit) {
        this.invited = invited;
        this.inviter = inviter;
        this.kit = kit;

        TextComponent textComponent = new TextComponent("");

        TextComponent lines = new TextComponent("▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆");
        lines.setBold(true);
        lines.setColor(ChatColor.DARK_AQUA);

        textComponent.addExtra(lines);
        textComponent.addExtra("\n\n");
        textComponent.addExtra(TextFormatter.highlight(String.format("You have been invited to duel **%s** with the **%s** kit!", inviter.getName(), kit.getName())));
        textComponent.addExtra("\n\n");

        TextComponent accept = new TextComponent("ACCEPT");
        accept.setColor(ChatColor.GREEN);
        accept.setBold(true);

        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to accept the duel request!").color(ChatColor.GREEN).create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/duel accept %s", inviter.getByDisguiseName())));

        textComponent.addExtra(accept);
        textComponent.addExtra(" ");

        TextComponent deny = new TextComponent("DENY");
        deny.setColor(ChatColor.RED);
        deny.setBold(true);

        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to deny the duel request!").color(ChatColor.RED).create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/duel deny %s", inviter.getByDisguiseName())));

        textComponent.addExtra(deny);
        textComponent.addExtra("\n");
        textComponent.addExtra(TextFormatter.convert("&cWARNING:&r This request will expire in one minute."));
        textComponent.addExtra("\n\n");
        textComponent.addExtra(lines);


        invited.sendMessage(textComponent);
        invited.playSound(invited.getLocation(), Sound.NOTE_PLING, 100, 2);
        invited.newIncoming(this);

        textComponent = new TextComponent("");

        textComponent.addExtra(lines);
        textComponent.addExtra("\n\n");
        textComponent.addExtra(TextFormatter.highlight(String.format("You have invited **%s** to a duel!", invited.getByDisguiseName())));
        textComponent.addExtra("\n\n");

        deny = new TextComponent("CANCEL");
        deny.setColor(ChatColor.RED);
        deny.setBold(true);

        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to cancel your duel request!").color(ChatColor.RED).create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/duel cancel %s", invited.getByDisguiseName())));

        textComponent.addExtra(deny);
        textComponent.addExtra("\n\n");
        textComponent.addExtra(lines);
        inviter.sendMessage(textComponent);
        inviter.newOutgoing(this);
        task = new BukkitRunnable() {
            @Override
            public void run() {
                expire();
            }
        }.runTaskLater(ServerAPI.getCore(), 1200);
    }

    private void expire() {
        if (invited != null) {
            if (invited.isOnline()) {
                invited.sendMessage(TextFormatter.pluginMessage("Duels", String.format("Your duel invite from **%s** expired.", inviter.getByDisguiseName())));
            }
            invited.removeIncoming(this);
        }
        inviter.removeOutgoing();
        inviter.sendMessage(TextFormatter.pluginMessage("Duels", String.format("Your duel invite to **%s** expired.", invited.getByDisguiseName())));
    }

    public void accept() {
        invited.removeIncoming(this);
        inviter.removeOutgoing();

        if (inviter.isOnline()) {
            List<DuelsMap> maps = DuelsAPI.getMaps().get("DUELS").getMaps().stream().filter(duelsMap -> duelsMap.getMapData().getString("duel").equalsIgnoreCase(kit.getMapType())).collect(Collectors.toList());
            DuelsMap map = maps.get(new Random().nextInt(maps.size()));

            Game game = new Game(invited, inviter, map, kit);
            inviter.setGame(game);
            LobbyListener.updateHeaderFooter(inviter, inviter.getCraft());
            invited.setGame(game);
            LobbyListener.updateHeaderFooter(invited, invited.getCraft());
            DuelsAPI.getGames().add(game);
        } else {
            invited.sendMessage(TextFormatter.pluginMessage("Duels", "That player is no longer online!"));
        }
        this.task.cancel();
    }

    public void reject() {
        invited.removeIncoming(this);
        invited.sendMessage(TextFormatter.pluginMessage("Duels", String.format("Your duel invite from **%s** was rejected.", inviter.getByDisguiseName())));
        if (inviter.isOnline()) {
            inviter.removeOutgoing();
            inviter.sendMessage(TextFormatter.pluginMessage("Duels", String.format("Your duel invite to **%s** was rejected.", invited.getByDisguiseName())));
        }
        this.task.cancel();
    }
    public void cancel() {
        if (invited.isOnline()) {
            invited.removeIncoming(this);
            invited.sendMessage(TextFormatter.pluginMessage("Duels", String.format("Your duel invite from **%s** was cancelled.", inviter.getByDisguiseName())));
        }
        inviter.removeOutgoing();
        inviter.sendMessage(TextFormatter.pluginMessage("Duels", String.format("Your duel invite to **%s** was cancelled.", invited.getByDisguiseName())));
        this.task.cancel();
    }

    public AuroraMCDuelsPlayer getInvited() {
        return invited;
    }

    public AuroraMCDuelsPlayer getInviter() {
        return inviter;
    }

    public Kit getKit() {
        return kit;
    }
}
