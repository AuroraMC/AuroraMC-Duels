package net.auroramc.duels.utils.damage;

import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.cosmetics.Cosmetic;
import net.auroramc.api.cosmetics.DeathEffect;
import net.auroramc.api.cosmetics.KillMessage;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.events.entity.PlayerDamageByEntityEvent;
import net.auroramc.core.api.events.entity.PlayerDamageByPlayerEvent;
import net.auroramc.core.api.events.entity.PlayerDamageByPlayerRangedEvent;
import net.auroramc.core.api.events.entity.PlayerDamageEvent;
import net.auroramc.core.api.events.player.PlayerItemConsumeEvent;
import net.auroramc.duels.AuroraMCDuels;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Game;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StandardDeathListener implements Listener {

    private static final List<Game> games;
    static {
        games = new ArrayList<>();
    }

    @EventHandler
    public void onDamage(PlayerDamageEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) e.getPlayer();
        if (player.isInGame() && games.contains(player.getGame())) {
            if (player.isSpectator()) {
                e.setCancelled(true);
                return;
            }
            if (e.getDamage() >= e.getPlayer().getHealth()) {
                e.setDamage(0);

                Entity entity = null;
                AuroraMCDuelsPlayer killer = null;
                KillMessage killMessage;
                KillMessage.KillReason killReason = KillMessage.KillReason.MELEE;
                if (player.getActiveCosmetics().containsKey(Cosmetic.CosmeticType.KILL_MESSAGE)) {
                    killMessage = (KillMessage) player.getActiveCosmetics().get(Cosmetic.CosmeticType.KILL_MESSAGE);
                } else {
                    killMessage = (KillMessage) AuroraMCAPI.getCosmetics().get(500);
                }
                if (e instanceof PlayerDamageByPlayerEvent) {

                    if (e instanceof PlayerDamageByPlayerRangedEvent) {
                        killer = (AuroraMCDuelsPlayer) ((PlayerDamageByPlayerRangedEvent) e).getDamager();
                        killReason = KillMessage.KillReason.BOW;
                        ((PlayerDamageByPlayerRangedEvent) e).getProjectile().remove();
                    } else {
                        killer = (AuroraMCDuelsPlayer) ((PlayerDamageByPlayerEvent) e).getDamager();
                        killer.getStats().incrementStatistic(4, "damageDealt", Math.round(e.getDamage() * 100), true);
                        switch (e.getCause()) {
                            case PROJECTILE: {
                                killReason = KillMessage.KillReason.BOW;
                                break;
                            }
                            case VOID: {
                                killReason = KillMessage.KillReason.VOID;
                                break;
                            }
                            case FALL: {
                                killReason = KillMessage.KillReason.FALL;
                                break;
                            }
                            case BLOCK_EXPLOSION: {
                                killReason = KillMessage.KillReason.TNT;
                                break;
                            }
                        }
                    }
                } else if (e instanceof PlayerDamageByEntityEvent) {
                    if (((PlayerDamageByEntityEvent) e).getDamager() instanceof TNTPrimed) {
                        TNTPrimed primed = (TNTPrimed) ((PlayerDamageByEntityEvent) e).getDamager();
                        if (primed.getSource() instanceof Player) {
                            Player damager = (Player) primed.getSource();
                            killer = (AuroraMCDuelsPlayer) ServerAPI.getPlayer(damager);
                            killReason = KillMessage.KillReason.TNT;
                        }
                    } else if (((PlayerDamageByEntityEvent) e).getDamager() instanceof Arrow) {
                        if (((Arrow) ((PlayerDamageByEntityEvent) e).getDamager()).getShooter() instanceof Entity) {
                            //Damage by entity.
                            entity = (Entity) ((Arrow) ((PlayerDamageByEntityEvent) e).getDamager()).getShooter();
                            killReason = KillMessage.KillReason.ENTITY;
                        }
                    } else {
                        entity = ((PlayerDamageByEntityEvent) e).getDamager();
                        killReason = KillMessage.KillReason.ENTITY;
                    }
                } else {
                    switch (e.getCause()) {
                        case FALL: {
                            killReason = KillMessage.KillReason.FALL;
                            if (player.getLastHitBy() != null && System.currentTimeMillis() - player.getLastHitAt() < 60000) {
                                killer = player.getLastHitBy();
                            }
                            break;
                        }
                        case VOID: {
                            killReason = KillMessage.KillReason.VOID;
                            if (player.getLastHitBy() != null && System.currentTimeMillis() - player.getLastHitAt() < 60000) {
                                killer = player.getLastHitBy();
                            }
                            break;
                        }
                        case LAVA: {
                            killReason = KillMessage.KillReason.LAVA;
                            if (player.getLastHitBy() != null && System.currentTimeMillis() - player.getLastHitAt() < 60000) {
                                killer = player.getLastHitBy();
                            }
                            break;
                        }
                        case FIRE_TICK: {
                            killReason = KillMessage.KillReason.FIRE;
                            if (player.getLastHitBy() != null && System.currentTimeMillis() - player.getLastHitAt() < 60000) {
                                killer = player.getLastHitBy();
                            }
                            break;
                        }
                        case DROWNING: {
                            killReason = KillMessage.KillReason.DROWNING;
                            if (player.getLastHitBy() != null && System.currentTimeMillis() - player.getLastHitAt() < 60000) {
                                killer = player.getLastHitBy();
                            }
                            break;
                        }
                        default: {
                            killReason = KillMessage.KillReason.UNKNOWN;
                        }
                    }
                }
                if (killer != null) {
                    if (killer.getActiveCosmetics().containsKey(Cosmetic.CosmeticType.KILL_MESSAGE)) {
                        killMessage = (KillMessage) killer.getActiveCosmetics().get(Cosmetic.CosmeticType.KILL_MESSAGE);
                    } else {
                        killMessage = (KillMessage) AuroraMCAPI.getCosmetics().get(500);
                    }
                    killer.getRewards().addXp("Kills", 25);
                    killer.getStats().incrementStatistic(4, "kills", 1, true);
                    killer.getStats().incrementStatistic(4, "kills;" + killReason.name(), 1, true);
                    if (!killer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(22))) {
                        killer.getStats().achievementGained(AuroraMCAPI.getAchievement(22), 1, true);
                    }

                    //If there is a killer, give out assists.
                    for (Map.Entry<AuroraMCDuelsPlayer, Long> entry : player.getLatestHits().entrySet()) {
                        if (System.currentTimeMillis() - entry.getValue() < 60000 && entry.getKey().getId() != killer.getId()) {
                            entry.getKey().getRewards().addXp("Assists", 10);
                            entry.getKey().sendMessage(TextFormatter.pluginMessage("Kill", "You got an assist on player **" + player.getByDisguiseName() + "**!"));
                            entry.getKey().playSound(entry.getKey().getLocation(), Sound.ARROW_HIT, 100, 1);
                        }
                    }
                }

                player.setLastHitAt(-1);
                player.setLastHitBy(null);
                player.getLatestHits().clear();
                player.setFireTicks(0);

                if (player.getActiveCosmetics().containsKey(Cosmetic.CosmeticType.DEATH_EFFECT)) {
                    ((DeathEffect) player.getActiveCosmetics().get(Cosmetic.CosmeticType.DEATH_EFFECT)).onDeath(player);
                }


                player.getGame().onDeath(player);
            } else {
                if (e instanceof PlayerDamageByPlayerEvent) {
                    if (((PlayerDamageByPlayerEvent) e).getDamager().equals(e.getPlayer())) {
                        e.setCancelled(true);
                    }
                }
                if (player.getGame().getGameState() != Game.GameState.IN_PROGRESS) {
                    e.setCancelled(true);
                }
                if (!e.isCancelled() && e instanceof PlayerDamageByPlayerEvent) {
                    AuroraMCDuelsPlayer player1 = (AuroraMCDuelsPlayer) ((PlayerDamageByPlayerEvent) e).getDamager();
                    long time = System.currentTimeMillis();
                    player.setLastHitBy(player1);
                    player.setLastHitAt(time);
                    player.getLatestHits().put(player1, time);
                    player1.getStats().incrementStatistic(4, "damageDealt", Math.round(e.getDamage() * 100), true);
                }
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.POTION) {
            Potion potion = Potion.fromItemStack(e.getItem());
            for (PotionEffect effect : potion.getEffects()) {
                e.getPlayer().addPotionEffect(effect);
            }
            e.setCancelled(true);
            e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
        }
    }

    public static void register(Game game) {
        games.add(game);
    }

    public static void deregister(Game game) {
        games.remove(game);
    }


}
