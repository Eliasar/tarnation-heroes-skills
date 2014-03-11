package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.PassiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Random;

public class SkillOneTwoPunch extends PassiveSkill {

    public SkillOneTwoPunch(Heroes plugin) {
        super(plugin, "OneTwoPunch");
        setDescription("You swing a second time for the damage of your normal punch.");
        setIdentifiers("skill onetwopunch");

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillCounterAttackListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("proc-chance", 10);
        node.set("particle-power", 0.5);
        node.set("particle-amount", 100);
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
            if (event.isCancelled()
                    || !(event.getDamager().getEntity() instanceof Player)) {
                return;
            }

            LivingEntity target = (LivingEntity) event.getEntity();
            Player player = (Player) event.getDamager().getEntity();
            Hero hero = plugin.getCharacterManager().getHero(player);
            int procChance = SkillConfigManager.getUseSetting(hero, SkillOneTwoPunch.this, "proc-chance", 10, false);

            // Skill check, damage check, hand check
            if (!hero.hasEffect("OneTwoPunch")
                    || !Skill.damageCheck(player, (LivingEntity) event.getEntity())
                    || player.getItemInHand().getType() != Material.AIR) {
                return;
            }

            // Get random number seeded by current system time
            Random rand = new Random(System.currentTimeMillis());
            int roll = rand.nextInt(100) + 1;

            // 1-100
            if (roll > 100 - procChance) {

                // One-two punch!
                Skill.damageEntity(target, hero.getEntity(), event.getDamage() * 2.0, EntityDamageEvent.DamageCause.ENTITY_ATTACK, true);
                broadcast(player.getLocation(), "You one-two punch "
                        + (target instanceof Player ? ((Player) target).getName() : target.getType())
                        + "!");
            }
        }
    }
}
