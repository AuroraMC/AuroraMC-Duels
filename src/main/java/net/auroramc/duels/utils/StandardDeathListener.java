package net.auroramc.duels.utils;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.cosmetics.Cosmetic;
import net.auroramc.core.api.cosmetics.DeathEffect;
import net.auroramc.core.api.cosmetics.KillMessage;
import net.auroramc.duels.AuroraMCDuels;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StandardDeathListener implements Listener {

    private static final List<Game> games;
    static {
        games = new ArrayList<>();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer((Player) e.getEntity());
            if (player.isInGame() && games.contains(player.getGame())) {
                if (e.getFinalDamage() > ((Player) e.getEntity()).getHealth()) {
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
                    if (e instanceof EntityDamageByEntityEvent) {
                        if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
                            Player damager = (Player) ((EntityDamageByEntityEvent) e).getDamager();
                            killer = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(damager);
                            killer.getStats().incrementStatistic(4, "damageDealt", Math.round(e.getFinalDamage() * 100), true);
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
                        } else if (((EntityDamageByEntityEvent) e).getDamager() instanceof TNTPrimed) {
                            TNTPrimed primed = (TNTPrimed) ((EntityDamageByEntityEvent) e).getDamager();
                            if (primed.getSource() instanceof Player) {
                                Player damager = (Player) primed.getSource();
                                killer = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(damager);
                                killReason = KillMessage.KillReason.TNT;
                            }
                        } else if (((EntityDamageByEntityEvent) e).getDamager() instanceof Arrow) {
                            Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) e).getDamager();
                            if (projectile.getShooter() instanceof Player) {
                                Player damager = (Player) projectile.getShooter();
                                killer = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(damager);
                                killReason = KillMessage.KillReason.BOW;
                            } else {
                                if (projectile.getShooter() instanceof Entity) {
                                    //Damage by entity.
                                    entity = (Entity) projectile.getShooter();
                                    killReason = KillMessage.KillReason.ENTITY;
                                }
                            }
                            projectile.remove();
                        } else {
                            //Damage by entity.
                            entity = ((EntityDamageByEntityEvent) e).getDamager();
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

                        if (!killer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(22))) {
                            killer.getStats().achievementGained(AuroraMCAPI.getAchievement(22), 1, true);
                        }

                        //If there is a killer, give out assists.
                        for (Map.Entry<AuroraMCDuelsPlayer, Long> entry : player.getLatestHits().entrySet()) {
                            if (System.currentTimeMillis() - entry.getValue() < 60000 && entry.getKey().getId() != killer.getId()) {
                                entry.getKey().getRewards().addXp("Assists", 10);
                                entry.getKey().getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Kill", "You got an assist on player **" + player.getPlayer().getName() + "**!"));
                                entry.getKey().getPlayer().playSound(entry.getKey().getPlayer().getLocation(), Sound.ARROW_HIT, 100, 1);
                            }
                        }
                    }

                    player.setLastHitAt(-1);
                    player.setLastHitBy(null);
                    player.getLatestHits().clear();
                    player.getPlayer().setFireTicks(0);

                    if (player.getActiveCosmetics().containsKey(Cosmetic.CosmeticType.DEATH_EFFECT)) {
                        ((DeathEffect)player.getActiveCosmetics().get(Cosmetic.CosmeticType.DEATH_EFFECT)).onDeath(player);
                    }

                    player.getGame().onDeath(player);
                }
            }
        }
    }

    public static void register(Game game) {
        games.add(game);
    }

    public static void deRegister(Game game) {
        games.remove(game);
    }


}
