package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class SkillBackstab extends PassiveSkill {

    public SkillBackstab(Heroes plugin) {
        super(plugin, "Backstab");
        setDescription("Attack from behind with an axe and deal 200% damage.");
        setIdentifiers("skill backstab");
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING);

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillBackstabListener(this), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.COOLDOWN.node(), 5000);
        node.set("particle-power", 0.5);
        node.set("particle-amount", 50);
        return node;
    }

    @Override
    public String getDescription(Hero hero) {
        String description = "";
        String ending = "§6; ";

        // Mana
        int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 0, false)
                - (SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA_REDUCE.node(), 0, false) * hero.getLevel());
        if (mana > 0) {
            description += "§6Cost: §9" + mana + "MP" + ending;
        }

        // Health cost
        int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false) -
                (SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getLevel());
        if (healthCost > 0 && mana > 0) {
            description += "§6" + healthCost + ending;
        } else if (healthCost > 0) {
            description += "§6Cost: §c" + healthCost + "HP" + ending;
        }

        // Cooldown
        int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false)
                - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getLevel()) / 1000;
        if (cooldown > 0) {
            description += "§6CD: §9" + cooldown + "s" + ending;
        }

        description += getDescription();

        return description;
    }

    public class SkillBackstabListener implements Listener {

        private Skill skill;

        public SkillBackstabListener (Skill skill) {
            this.skill = skill;
        }

        @EventHandler
        public void onWeaponDamage(WeaponDamageEvent event) {
            if (event.isCancelled()
                    || !(event.getDamager() instanceof Hero)) {
                return;
            }

            Hero hero;

            if (event.getDamager() instanceof Hero) hero = (Hero) event.getDamager();
            else hero = plugin.getCharacterManager().getHero((Player) event.getDamager());

            if (!hero.hasEffect("backstab")) return;

            Player player = hero.getPlayer();
            LivingEntity target = (LivingEntity) event.getEntity();
            Location targetLocation = target.getLocation();

            Map<String, Long> cooldowns = hero.getCooldowns();

            double angle = Math.toDegrees(player.getLocation().getDirection().angle(targetLocation.getDirection()));
            if ((angle >= 0 && angle <= 80)
                    || (angle >= 280 && angle <= 360)) {
                if (!cooldowns.containsKey("backstab")
                        || (hero.getCooldown("backstab") - System.currentTimeMillis()) <= 0) {
                    if (target instanceof Player) {
                        broadcast(player.getLocation(), player.getName() + " has backstabbed " + ((Player) target).getName() + "!");
                    } else {
                        broadcast(player.getLocation(), player.getName() + " has backstabbed " + target.getType() + "!");
                    }

                    // Set damage
                    event.setDamage(event.getDamage() * 2);

                    // Play effect at target
                    playEffect(hero, target);

                    hero.setCooldown(
                            skill.getName(),
                            System.currentTimeMillis() + SkillConfigManager.getUseSetting(hero, skill, SkillSetting.COOLDOWN, 5000, false)
                    );
                }
            }
        }

        public void playEffect(Hero hero, LivingEntity target) {
            float particlePower = (float) SkillConfigManager.getUseSetting(hero, skill, "particle-power", 0.5, false);
            int particleAmount = SkillConfigManager.getUseSetting(hero, skill, "particle-amount", 50, false);
            Location loc = target.getLocation();
            loc.setY(loc.getY() + 0.5);

            loc.getWorld().spigot().playEffect(loc, Effect.CRIT, 0, 0, 0, 0, 0, particlePower, particleAmount, 64);
        }
    }
}
