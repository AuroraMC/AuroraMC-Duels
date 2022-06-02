package net.auroramc.duels.kits;

import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Game;
import net.auroramc.duels.api.game.Kit;
import net.auroramc.duels.utils.damage.NoDamageListener;
import net.auroramc.duels.utils.damage.StandardDeathListener;
import net.auroramc.duels.utils.settings.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Sumo extends Kit {

    public Sumo() {
        super(4, "Sumo", "Sumo", Material.GOLD_BARDING, (short)0, "SUMO");
    }

    @Override
    public void onGameCreate(Game game) {
        NoDamageListener.register(game);
        DisableHungerListener.register(game);
        DisableBreakListener.register(game);
        DisablePlaceListener.register(game);
        DisablePickupListener.register(game);
        DisableDropListener.register(game);
    }

    @Override
    public void onGameRemove(Game game) {
        NoDamageListener.deregister(game);
        DisableHungerListener.deregister(game);
        DisableBreakListener.deregister(game);
        DisablePlaceListener.deregister(game);
        DisablePickupListener.deregister(game);
        DisableDropListener.deregister(game);
    }

    @Override
    public void onGameStart(AuroraMCDuelsPlayer player) {
    }
}
