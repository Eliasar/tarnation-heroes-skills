package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Random;

public class SkillCounterAttack extends PassiveSkill {

    public SkillCounterAttack(Heroes plugin) {
        super(plugin, "CounterAttack");
        setDescription("You have a $1% chance to return 50% of the attack's damage while blocking.");
        setIdentifiers("skill counterattack");

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillCounterAttackListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("block-chance", 5);
        node.set("particle-power", 0.5);
        node.set("particle-amount", 100);
        return node;
    }

    @Override
    public String getDescription(Hero hero) {
        String description = getDescription();

        // Block chance
        int blockChance = SkillConfigManager.getUseSetting(hero, this, "block-chance", 5, false);

        return description.replace("$1", "§9" + blockChance + "§6");
    }

    public class SkillCounterAttackListener implements Listener {

        @EventHandler
        public void onWeaponDamage(WeaponDamageEvent event) {
            if (event.isCancelled()) return;

            Player player = (Player) event.getEntity();
            Hero hero = plugin.getCharacterManager().getHero(player);
            LivingEntity attacker = (LivingEntity) event.getDamager();
            int blockChance = SkillConfigManager.getUseSetting(hero, SkillCounterAttack.this, "block-chance", 5, false);

            // Skill check and damage check
            if (!hero.hasEffect("CounterAttack") && !Skill.damageCheck(hero.getPlayer(), attacker)) return;

            // Check if hero is blocking and attacker is within 3 blocks
            if (!hero.getPlayer().isBlocking() || event.getEntity().getLocation().distance(player.getLocation()) > 3) return;

            // Get random number seeded by current system time
            Random rand = new Random(System.currentTimeMillis());
            int roll = rand.nextInt(100) + 1;

            // 1-100
            if (roll > 100 - blockChance) {
                // Dodge, dip, duck, dive, and dodge
                event.setCancelled(true);
                Skill.damageEntity(attacker, hero.getEntity(), event.getDamage() * 0.5, EntityDamageEvent.DamageCause.ENTITY_ATTACK, true);
                broadcast(player.getLocation(), "You have countered "
                        + (attacker instanceof Player ? ((Player) attacker).getName() : attacker.getType())
                        + "!");

                // Play particle effect
                playEffect(hero);
            }
        }

        @EventHandler
        public void onSkillDamage(SkillDamageEvent event) {
            if (event.isCancelled()) return;

            Player player = (Player) event.getEntity();
            Hero hero = plugin.getCharacterManager().getHero(player);
            LivingEntity attacker = (LivingEntity) event.getDamager();
            int blockChance = SkillConfigManager.getUseSetting(hero, SkillCounterAttack.this, "block-chance", 5, false);

            // Skill check and damage check
            if (!hero.hasEffect("CounterAttack") && !Skill.damageCheck(hero.getPlayer(), attacker)) return;

            // Check if hero is blocking and attacker is within 3 blocks
            if (!hero.getPlayer().isBlocking() || event.getEntity().getLocation().distance(player.getLocation()) > 3) return;

            // Get random number seeded by current system time
            Random rand = new Random(System.currentTimeMillis());
            int roll = rand.nextInt(100) + 1;

            // 1-100
            if (roll > 100 - blockChance) {
                // Dodge, dip, duck, dive, and dodge
                event.setCancelled(true);
                Skill.damageEntity(attacker, hero.getEntity(), event.getDamage() * 0.5, EntityDamageEvent.DamageCause.ENTITY_ATTACK, true);
                broadcast(player.getLocation(), "You have countered "
                        + (attacker instanceof Player ? ((Player) attacker).getName() : attacker.getType())
                        + "!");

                // Play particle effect
                playEffect(hero);
            }
        }

        public void playEffect(Hero hero) {
            float particlePower = (float) SkillConfigManager.getUseSetting(hero, SkillCounterAttack.this, "particle-power", 0.5, false);
            int particleAmount = SkillConfigManager.getUseSetting(hero, SkillCounterAttack.this, "particle-amount", 100, false);
            Location loc = hero.getPlayer().getLocation();
            loc.setY(loc.getY() + 0.5);

            hero.getPlayer().getWorld().spigot().playEffect(loc, Effect.COLOURED_DUST, 0, 0, 0, 0, 0, particlePower, particleAmount, 64);
        }
    }
}
