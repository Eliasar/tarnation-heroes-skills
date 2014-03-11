package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkillSafeFall extends PassiveSkill {

    public SkillSafeFall(Heroes plugin) {
        super(plugin, "SafeFall");
        setDescription("Take one-third damage from falls.\n");
        setIdentifiers("skill safefall");

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillSafeFallListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("particle-power", 0.5);
        node.set("particle-amount", 10);
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
        public void onWeaponDamage(WeaponDamageEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
                return;
            }

            //Player player = (Player) event.getEntity();
            Hero hero = plugin.getCharacterManager().getHero((Player) event.getEntity());

            if (!hero.hasEffect("SafeFall")) return;

            event.setDamage(event.getDamage() * 0.33);
        }
    }
}
