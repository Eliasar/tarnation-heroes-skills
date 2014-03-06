package com.herocraftonline.heroes.characters.skill.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
            //plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
            final Player player = event.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, 1));
            repeatingTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, 1));
                        }
                    }, 0L, 405L);
        }

        // Cancel event, apply effect, and start timer
        @EventHandler
        public void onPlayerRespawn(PlayerRespawnEvent event) {
            plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
            final Player player = event.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, 1));
            repeatingTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, 1));
                        }
                    }, 0L, 405L);
        }

        // Cancel event, apply effect, and start timer
        @EventHandler
        public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
            plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
            final Player player = event.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, 1));
            repeatingTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 400, 1));
                        }
                    }, 0L, 405L);
        }

        // Cancel event
        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            plugin.getServer().getScheduler().cancelTask(repeatingTaskID);
        }
    }
}
