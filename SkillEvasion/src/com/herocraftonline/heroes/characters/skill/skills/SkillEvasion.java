package com.herocraftonline.heroes.characters.skill.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class SkillEvasion extends PassiveSkill {

    public SkillEvasion(Heroes plugin) {
        super(plugin, "Evasion");
        setDescription("You have a $1% chance to evade weapon damage and skills.");
        setIdentifiers("skill evasion");

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillEvasionListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.AMOUNT.node(), 10);
        node.set("particle-name", "spell");
        node.set("particle-power", 0.5);
        node.set("particle-amount", 10);
        return node;
    }

    @Override
    public String getDescription(Hero hero) {
        String description = "";

        // Dodge chance
        int dodgeChance = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT, 10, false);
        description += getDescription().replace("$1", "§9" + dodgeChance + "§6");

        return description;
    }

    public class SkillEvasionListener implements Listener {

        public SkillEvasionListener() { }

        @EventHandler(ignoreCancelled = true)
        public void onWeaponDamage(WeaponDamageEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
                return;
            }

            Player player = (Player) event.getEntity();
            Hero hero = plugin.getCharacterManager().getHero(player);
            double amount = SkillConfigManager.getUseSetting(hero, SkillEvasion.this, SkillSetting.AMOUNT.node(), 10, false);
            amount = amount > 0 ? amount : 0;

            // Get random number seeded by current system time
            Random rand = new Random(System.currentTimeMillis());

            // 1-100
            if (rand.nextInt(100) + 1 > 100 - amount) {
                // Dodge, dip, duck, dive, and dodge
                event.setCancelled(true);
                broadcast(player.getLocation(), "You have evaded the attack!");

                // Play particle effect
                playEffect(hero);
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onSkillDamage(SkillDamageEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
                return;
            }

            Player player = (Player) event.getEntity();
            Hero hero = plugin.getCharacterManager().getHero(player);
            double amount = SkillConfigManager.getUseSetting(hero, SkillEvasion.this, SkillSetting.AMOUNT.node(), 10, false);
            amount = amount > 0 ? amount : 0;

            // Get random number seeded by current system time
            Random rand = new Random(System.currentTimeMillis());

            // 1-100
            if (rand.nextInt(100) + 1 > 100 - amount) {
                // Dodge, dip, duck, dive, and dodge
                event.setCancelled(true);
                broadcast(player.getLocation(), "You have evaded the attack!");

                // Play particle effect
                playEffect(hero);
            }
        }

        public void playEffect(Hero hero) {
            String particleName = SkillConfigManager.getUseSetting(hero, SkillEvasion.this, "particle-name", "magicCrit");
            float particlePower = (float) SkillConfigManager.getUseSetting(hero, SkillEvasion.this, "particle-power", 0.5, false);
            int particleAmount = SkillConfigManager.getUseSetting(hero, SkillEvasion.this, "particle-amount", 10, false);
            ParticleEffect pe = new ParticleEffect(particleName, hero.getPlayer().getLocation(), particlePower, particleAmount);
            try {
                pe.playEffect();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
