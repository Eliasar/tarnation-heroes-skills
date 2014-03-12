package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.common.SlowEffect;
import com.herocraftonline.heroes.characters.skill.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class SkillFrostbolt extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillFrostbolt(Heroes plugin) {
        super(plugin, "Frostbolt");
        setDescription("Fire a bolt of frost that deals $1 damage and slows the target.");
        setUsage("/skill frostbolt");
        setArgumentRange(0, 0);
        setIdentifiers("skill frostbolt");
        setTypes(SkillType.ICE, SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.DEBUFF);

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillFrostboltListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.DAMAGE.node(), 5);
        node.set(SkillSetting.DAMAGE_INCREASE.node(), 0.25);
        node.set(SkillSetting.MANA.node(), 15);
        node.set(SkillSetting.COOLDOWN.node(), 5000);
        node.set("slow-duration", 2500);
        node.set("slow-multiplier", 2);
        node.set("velocity", 4);
        node.set(SkillSetting.APPLY_TEXT.node(), "$1 has been slowed!");
        node.set(SkillSetting.EXPIRE_TEXT.node(), "$1 is no longer slowed.");
        node.set("particle-power", 1);
        node.set("particle-amount", 50);
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = SkillConfigManager.getRaw(this, SkillSetting.APPLY_TEXT.node(), "$1 has been slowed!");
        expireText = SkillConfigManager.getRaw(this, SkillSetting.EXPIRE_TEXT.node(), "$1 is no longer slowed.");
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

        // Damage
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 0, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.0, false) * hero.getLevel();

        description += getDescription().replace("$1", "§9" + damage + "§6");

        return description;
    }

    @Override
    public SkillResult use(Hero hero, String[] strings) {
        double velocity = SkillConfigManager.getUseSetting(hero, this, "velocity", 4, false);
        Vector velocityVector = hero.getPlayer().getEyeLocation().getDirection().multiply(velocity);
        hero.getPlayer().launchProjectile(Snowball.class).setVelocity(velocityVector);
        return SkillResult.NORMAL;
    }

    /*public class FrostboltEffect extends SlowEffect {

        public FrostboltEffect(Skill skill, long duration, int amplifier, Hero applier) {
            super(skill, duration, amplifier, false, applyText, expireText, applier);
        }

        @Override
        public void applyToHero(Hero hero) {
            super.applyToHero(hero);
        }

        @Override
        public void applyToMonster(Monster monster) {
            super.applyToMonster(monster);
        }

        @Override
        public void removeFromHero(Hero hero) {
            super.removeFromHero(hero);
        }

        @Override
        public void removeFromMonster(Monster monster) {
            super.removeFromMonster(monster);
        }
    }*/

    public class SkillFrostboltListener implements Listener {

        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {

            if (event.isCancelled()
                    || !(event instanceof EntityDamageByEntityEvent)
                    || !(event.getEntity() instanceof LivingEntity)) return;

            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            Entity projectile = subEvent.getDamager();

            if (!(projectile instanceof Snowball)) return;

            Entity damager = (Entity) ((Snowball) subEvent.getDamager()).getShooter();

            if (damager instanceof Player) {
                Hero hero = plugin.getCharacterManager().getHero((Player) damager);
                Player shooter = hero.getPlayer();
                LivingEntity target = (LivingEntity) event.getEntity();

                if (!Skill.damageCheck(shooter, target)) {
                    event.setCancelled(true);
                    return;
                }

                // Extinguish target
                target.setFireTicks(0);

                // Damage
                double damage = SkillConfigManager.getUseSetting(hero, SkillFrostbolt.this, SkillSetting.DAMAGE.node(), 3, false)
                        + SkillConfigManager.getUseSetting(hero, SkillFrostbolt.this, SkillSetting.DAMAGE_INCREASE.node(), 0.2, false) * hero.getPlayer().getLevel();
                event.setDamage(damage);

                // Add effect
                long duration = SkillConfigManager.getUseSetting(hero, SkillFrostbolt.this, "slow-duration", 2500, false);
                int amplifier = SkillConfigManager.getUseSetting(hero, SkillFrostbolt.this, "slow-multiplier", 2, false);
                //FrostboltEffect fe = new FrostboltEffect(SkillFrostbolt.this, duration, amplifier, hero);

                plugin.getCharacterManager().getCharacter(target).addEffect(
                        new SlowEffect(SkillFrostbolt.this, duration, amplifier, false, applyText, expireText, hero)
                );

                float particlePower = SkillConfigManager.getUseSetting(hero, SkillFrostbolt.this, "particle-power", 1, false);
                int particleAmount = SkillConfigManager.getUseSetting(hero, SkillFrostbolt.this, "particle-amount", 50, false);

                // Particle effect
                Location loc = target.getLocation();
                loc.setY(loc.getY() + 0.5);
                shooter.getWorld().spigot().playEffect(loc, Effect.SNOWBALL_BREAK, 0, 0, 0, 0, 0, particlePower, particleAmount, 64);
            }
        }
    }
}
