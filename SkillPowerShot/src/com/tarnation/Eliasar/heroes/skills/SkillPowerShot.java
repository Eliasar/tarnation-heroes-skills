package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.*;
import com.tarnation.Eliasar.util.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkillPowerShot extends ActiveSkill {

    public SkillPowerShot(Heroes plugin) {
        super(plugin, "PowerShot");
        setDescription("Empower your next shot within 10 seconds to deal 250% weapon damage.");
        setUsage("/skill powershot");
        setArgumentRange(0, 0);
        setIdentifiers("skill powershot");
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING);

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillPowerShotListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.COOLDOWN.node(), 20000);
        node.set(SkillSetting.DURATION.node(), 10000);
        node.set(SkillSetting.MANA.node(), 30);
        node.set(SkillSetting.APPLY_TEXT.node(), "$1 has gained Power Shot.");
        node.set(SkillSetting.EXPIRE_TEXT.node(), "$1 has lost Power Shot.");
        node.set("minimum-draw-timer", 5000);
        node.set("minimum-draw-timer-per-level", 33.33);
        node.set("particle-name", "depthsuspend");
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

    @Override
    public SkillResult use(Hero hero, String[] strings) {
        PowerShotEffect powerShotEffect = new PowerShotEffect(this, SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 10000, false));
        hero.addEffect(powerShotEffect);

        // Broadcast
        broadcastExecuteText(hero);

        return SkillResult.NORMAL;
    }

    public class PowerShotEffect extends ExpirableEffect {

        private int particleEffectTaskID;

        public PowerShotEffect(Skill skill, long duration) {
            super(skill, "PowerShotEffect", duration);
        }

        @Override
        public void applyToHero(Hero hero) {
            super.applyToHero(hero);
            Player p = hero.getPlayer();
            final Hero finalHero = hero;
            broadcast(p.getLocation(), SkillConfigManager.getUseSetting(hero,
                    SkillPowerShot.this, SkillSetting.APPLY_TEXT, "").replace("$1", p.getDisplayName()));
            particleEffectTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
                    plugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            playEffect(finalHero);
                        }
                    },
                    0L,
                    20L
            );
        }

        @Override
        public void removeFromHero(Hero hero) {
            super.removeFromHero(hero);
            Player p = hero.getPlayer();
            broadcast(p.getLocation(), SkillConfigManager.getUseSetting(hero,
                    SkillPowerShot.this, SkillSetting.EXPIRE_TEXT, "").replace("$1", p.getDisplayName()));
            plugin.getServer().getScheduler().cancelTask(particleEffectTaskID);
        }

        public void playEffect(Hero hero) {
            String particleName = SkillConfigManager.getUseSetting(hero, SkillPowerShot.this, "particle-name", "depthsuspend");
            float particlePower = (float) SkillConfigManager.getUseSetting(hero, SkillPowerShot.this, "particle-power", 10, false);
            int particleAmount = SkillConfigManager.getUseSetting(hero, SkillPowerShot.this, "particle-amount", 50, false);
            Location loc = hero.getPlayer().getLocation();
            loc.setY(loc.getY() + 0.5);
            ParticleEffect pe = new ParticleEffect(particleName, loc, particlePower, particleAmount);
            pe.playEffect();
        }
    }

    public class SkillPowerShotListener implements Listener {

        @EventHandler
        public void onWeaponDamage(WeaponDamageEvent event) {

            if (event.isCancelled()
                    || !event.getAttackerEntity().getType().equals(EntityType.ARROW)
                    || !(event.getDamager().getEntity() instanceof Player)) {
                return;
            }

            Hero hero = plugin.getCharacterManager().getHero((Player) event.getDamager().getEntity());

            if (hero.hasEffect("PowerShotEffect")) {
                // Remove effect
                hero.removeEffect(hero.getEffect("PowerShotEffect"));

                // Set damage to 250%
                event.setDamage(event.getDamage() * 2.5);
            }
        }
    }
}
