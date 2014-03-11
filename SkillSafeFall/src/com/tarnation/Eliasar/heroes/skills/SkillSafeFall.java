package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.CharacterDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class SkillSafeFall extends PassiveSkill {

    public SkillSafeFall(Heroes plugin) {
        super(plugin, "SafeFall");
        setDescription("Take one-third damage from falls.");
        setIdentifiers("skill safefall");

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillSafeFallListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        return node;
    }

    @Override
    public String getDescription(Hero hero) {
        String description = "";

        description += getDescription();

        return description;
    }

    public class SkillSafeFallListener implements Listener {

        @EventHandler
        public void onFallDamage(CharacterDamageEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Player) || event.getCause() != EntityDamageEvent.DamageCause.FALL) {
                return;
            }

            //Player player = (Player) event.getEntity();
            Hero hero = plugin.getCharacterManager().getHero((Player) event.getEntity());

            if (!hero.hasEffect("SafeFall")) return;

            event.setDamage(event.getDamage() * 0.33);
        }
    }
}
