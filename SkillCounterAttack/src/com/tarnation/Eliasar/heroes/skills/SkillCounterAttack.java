package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Random;

public class SkillCounterAttack extends PassiveSkill {

    public SkillCounterAttack(Heroes plugin) {
        super(plugin, "CounterAttack");
        setDescription("You have a 5% chance to return 50% of the attack's damage while blocking.");
        setIdentifiers("skill counterattack");

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillCounterAttackListener(), plugin);
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
        String description = getDescription();
        return description;
    }

    public class SkillCounterAttackListener implements Listener {

        @EventHandler
        public void onWeaponDamage(WeaponDamageEvent event) {
            if (event.isCancelled()) return;

            Player player = (Player) event.getEntity();
            Hero hero = plugin.getCharacterManager().getHero(player);
            //LivingEntity attacker = (LivingEntity) event.getDamager();

            broadcast(player.getLocation(), "[CA] Checked.");

            // Skill check
            if (!hero.hasEffect("CounterAttack")) return;

            // Check if hero is blocking and attacker is within 3 blocks
            if (!hero.getPlayer().isBlocking() || event.getEntity().getLocation().distance(player.getLocation()) > 3) return;

            // Get random number seeded by current system time
            Random rand = new Random(System.currentTimeMillis());
            int roll = rand.nextInt(100) + 1;
            broadcast(player.getLocation(), "[CA] roll = " + roll);

            // 1-100
            if (rand.nextInt(100) + 1 > 100 - 5) {
                // Dodge, dip, duck, dive, and dodge
                event.setCancelled(true);
                broadcast(player.getLocation(), "You have countered "
                        + (event.getEntity() instanceof Player ? ((Player)event.getEntity()).getName() : event.getEntity().getType())
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
            //LivingEntity attacker = (LivingEntity) event.getDamager();

            // Skill check
            if (!hero.hasEffect("CounterAttack")) return;

            // Check if hero is blocking and attacker is within 3 blocks
            if (!hero.getPlayer().isBlocking() || event.getEntity().getLocation().distance(player.getLocation()) > 3) return;

            // Get random number seeded by current system time
            Random rand = new Random(System.currentTimeMillis());

            // 1-100
            if (rand.nextInt(100) + 1 > 100 - 5) {
                // Dodge, dip, duck, dive, and dodge
                event.setCancelled(true);
                broadcast(player.getLocation(), "You have countered "
                        + (event.getEntity() instanceof Player ? ((Player)event.getEntity()).getName() : event.getEntity().getType())
                        + "!");

                // Play particle effect
                playEffect(hero);
            }
        }

        public void playEffect(Hero hero) {
            float particlePower = (float) SkillConfigManager.getUseSetting(hero, SkillCounterAttack.this, "particle-power", 0.5, false);
            int particleAmount = SkillConfigManager.getUseSetting(hero, SkillCounterAttack.this, "particle-amount", 10, false);
            Location loc = hero.getPlayer().getLocation();
            loc.setY(loc.getY() + 0.5);

            hero.getPlayer().getWorld().spigot().playEffect(loc, Effect.COLOURED_DUST, 0, 0, 0, 0, 0, particlePower, particleAmount, 64);
        }
    }
}
