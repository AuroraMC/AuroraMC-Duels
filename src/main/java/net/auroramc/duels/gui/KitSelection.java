package net.auroramc.duels.gui;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.game.DuelInvite;
import net.auroramc.duels.api.game.Kit;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class KitSelection extends GUI {

    private AuroraMCDuelsPlayer player;
    private AuroraMCDuelsPlayer invitee;

    public KitSelection(AuroraMCDuelsPlayer player, AuroraMCDuelsPlayer invitee) {
        super("&3&lSelect a kit!", 5, true);
        border("&3&lSelect a kit!", null);

        this.invitee = invitee;
        this.player = player;

        int column = 1;
        int row = 1;

        for (Kit kit : DuelsAPI.getRegisteredKits()) {
            this.setItem(row, column, new GUIItem(kit.getMaterial(), "&3&l" + kit.getName(), 1, ";&7" + WordUtils.wrap(kit.getDescription(), 40, ";&7", false) + ";;&aClick to send invite!"));
            column++;
            if (column == 8) {
                row++;
                column = 1;
                if (row == 5) {
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        if (item.getType() == Material.STAINED_GLASS_PANE) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
            return;
        }

        Kit kit = DuelsAPI.getRegisteredKits().get(((row - 1) * 7) + (column - 1));
        if (invitee.getPlayer().isOnline()) {
            player.getPlayer().closeInventory();
            new BukkitRunnable(){
                @Override
                public void run() {
                    new DuelInvite(player, invitee, kit);
                }
            }.runTaskAsynchronously(AuroraMCAPI.getCore());
        } else {
            player.getPlayer().closeInventory();
            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", "The player you invited has since gone offline so your invite was cancelled!"));
        }
    }

}
