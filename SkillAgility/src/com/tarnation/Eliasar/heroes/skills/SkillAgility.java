package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.ClassChangeEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SkillAgility extends PassiveSkill {

    public SkillAgility(Heroes plugin) {
        super(plugin, "Agility");
        setDescription("Your agility training has granted you more powerful legs.");
        setIdentifiers("skill agility");
        setTypes(SkillType.BUFF, SkillType.MOVEMENT);

        // Register listener
        Bukkit.getServer().getPluginManager().registerEvents(new SkillAgilityListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        return super.getDefaultConfig();
    }

    @Override
    public String getDescription(Hero hero) {
        return getDescription();
    }

    public class SkillAgilityListener implements Listener {

        private int repeatingTaskID;

        // Apply effect and start timer
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            broadcast(event.getPlayer().getLocation(), "[Agility] PlayerJoinEvent fired.");
            if (!plugin.getCharacterManager().getHero(event.getPlayer()).hasEffect("Agility")) return;

            final Player player = event.getPlayer();
            player.removePotionEffect(PotionEffectType.JUMP);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1));
            repeatingTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1));
                        }
                    }, 0L, 105L);
        }

        // Cancel event, apply effect, and start timer
        @EventHandler
        public void onPlayerRespawn(PlayerRespawnEvent event) {
            broadcast(event.getPlayer().getLocation(), "[Agility] PlayerRespawnEvent fired.");
            if (!plugin.getCharacterManager().getHero(event.getPlayer()).hasEffect("Agility")) return;

            plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
            final Player player = event.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1));
            repeatingTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1));
                        }
                    }, 0L, 105L);
        }

        // Cancel event, apply effect, and start timer
        @EventHandler
        public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
            broadcast(event.getPlayer().getLocation(), "[Agility] PlayerChangedWorldEvent fired.");
            if (!plugin.getCharacterManager().getHero(event.getPlayer()).hasEffect("Agility")) return;

            plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
            final Player player = event.getPlayer();
            player.removePotionEffect(PotionEffectType.JUMP);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1));
            repeatingTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1));
                        }
                    }, 0L, 105L);
        }

        // Cancel event
        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            broadcast(event.getPlayer().getLocation(), "[Agility] PlayerQuitEvent fired.");
            if (!plugin.getCharacterManager().getHero(event.getPlayer()).hasEffect("Agility")) return;

            final Player player = event.getPlayer();
            plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
            player.removePotionEffect(PotionEffectType.JUMP);
        }

        @EventHandler
        public void onClassChange(ClassChangeEvent event) {
            broadcast(event.getHero().getPlayer().getLocation(), "[Agility] ClassChangeEvent fired.");
            final Player player = event.getHero().getPlayer();
            plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
            player.removePotionEffect(PotionEffectType.JUMP);
            if (event.getHero().hasEffect("Agility")) {
                plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
                player.removePotionEffect(PotionEffectType.JUMP);
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1));
                repeatingTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                        new Runnable() {
                            @Override
                            public void run() {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1));
                            }
                        }, 0L, 105L);
            } else {
                plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
                player.removePotionEffect(PotionEffectType.JUMP);
            }
        }

        @EventHandler
        public void onLevelUp(PlayerLevelChangeEvent event) {
            broadcast(event.getPlayer().getLocation(), "[Agility] PlayerLevelChangeEvent fired.");
            final Player player = event.getPlayer();
            plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
            player.removePotionEffect(PotionEffectType.JUMP);
            if (plugin.getCharacterManager().getHero(event.getPlayer()).hasEffect("Agility")) {
                plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
                player.removePotionEffect(PotionEffectType.JUMP);
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1));
                repeatingTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                        new Runnable() {
                            @Override
                            public void run() {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1));
                            }
                        }, 0L, 105L);
            } else {
                plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
                player.removePotionEffect(PotionEffectType.JUMP);
            }
        }
    }
}
