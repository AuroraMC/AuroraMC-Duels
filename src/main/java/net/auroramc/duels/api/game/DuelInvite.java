package net.auroramc.duels.api.game;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.DuelsMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
        textComponent.addExtra(String.format(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight("You have been invited to duel **%s**'s with the **%s** kit!")), inviter.getPlayer().getName(), kit.getName()));
        textComponent.addExtra("\n\n");

        TextComponent accept = new TextComponent("ACCEPT");
        accept.setColor(ChatColor.GREEN);
        accept.setBold(true);

        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(AuroraMCAPI.getFormatter().convert("&aClick here to accept the duel request!")).create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/duel accept %s", inviter.getPlayer().getName())));

        textComponent.addExtra(accept);
        textComponent.addExtra(" ");

        TextComponent deny = new TextComponent("DENY");
        deny.setColor(ChatColor.RED);
        deny.setBold(true);

        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(AuroraMCAPI.getFormatter().convert("&cClick here to deny the duel request!")).create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/duel deny %s", inviter.getPlayer().getName())));

        textComponent.addExtra(deny);
        textComponent.addExtra("\n");
        textComponent.addExtra(AuroraMCAPI.getFormatter().convert("&cWARNING:&r This request will expire in one minute."));
        textComponent.addExtra("\n\n");
        textComponent.addExtra(lines);


        invited.getPlayer().getPlayer().spigot().sendMessage(textComponent);
        invited.newIncoming(this);

        textComponent = new TextComponent("");

        textComponent.addExtra(lines);
        textComponent.addExtra("\n\n");
        textComponent.addExtra(String.format(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight("You have invited **%s** to a duel!")), invited.getPlayer().getName()));
        textComponent.addExtra("\n\n");

        deny = new TextComponent("CANCEL");
        deny.setColor(ChatColor.RED);
        deny.setBold(true);

        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(AuroraMCAPI.getFormatter().convert("&cClick here to cancel your duel request!")).create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/duel cancel %s", invited.getPlayer().getName())));

        textComponent.addExtra(deny);
        textComponent.addExtra("\n\n");
        textComponent.addExtra(lines);
        inviter.getPlayer().getPlayer().spigot().sendMessage(textComponent);
        inviter.newOutgoing(this);
        task = new BukkitRunnable() {
            @Override
            public void run() {
                expire();
            }
        }.runTaskLater(AuroraMCAPI.getCore(), 1200);
    }

    private void expire() {
        if (invited.getPlayer() != null) {
            if (invited.getPlayer().isOnline()) {
                invited.getPlayer().getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", String.format("Your duel invite from **%s** expired.", invited.getPlayer().getName())));
            }
            invited.removeIncoming(this);
        }
        inviter.removeOutgoing();
    }

    public void accept() {
        invited.removeIncoming(this);
        inviter.removeOutgoing();

        List<DuelsMap> maps = DuelsAPI.getMaps().get("DUELS").getMaps().stream().filter(duelsMap -> duelsMap.getMapData().getString("DUEL").equalsIgnoreCase(kit.getMapType())).collect(Collectors.toList());
        DuelsMap map = maps.get(new Random().nextInt(maps.size()));

        Game game = new Game(invited, inviter, map, kit);
        inviter.setGame(game);
        invited.setGame(game);
        DuelsAPI.getGames().add(game);
        this.task.cancel();
    }

    public void reject() {
        invited.removeIncoming(this);
        invited.getPlayer().getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", String.format("Your duel invite from **%s** was rejected.", inviter.getPlayer().getName())));
        inviter.removeOutgoing();
        inviter.getPlayer().getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", String.format("Your duel invite to **%s** was rejected.", invited.getPlayer().getName())));
        this.task.cancel();
    }
    public void cancel() {
        invited.removeIncoming(this);
        invited.getPlayer().getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", String.format("Your duel invite from **%s** was cancelled.", inviter.getPlayer().getName())));
        inviter.removeOutgoing();
        inviter.getPlayer().getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", String.format("Your duel invite to **%s** was cancelled.", invited.getPlayer().getName())));
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
