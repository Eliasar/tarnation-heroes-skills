package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkillJab extends ActiveSkill {

    public SkillJab(Heroes plugin) {
        super(plugin, "Jab");
        setDescription("You jab your opponent for 120% fist damage.");
        setUsage("/skill jab");
        setArgumentRange(0, 0);
        setIdentifiers("skill jab");
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING);

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillAssaultListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.COOLDOWN.node(), 1500);
        node.set(SkillSetting.DURATION.node(), 10000);
        node.set(SkillSetting.MANA.node(), 10);
        node.set(SkillSetting.APPLY_TEXT.node(), "$1 has gained Jab.");
        node.set(SkillSetting.EXPIRE_TEXT.node(), "$1 has lost Jab.");
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
        JabEffect jabEffect = new JabEffect(this, SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 10000, false));
        hero.addEffect(jabEffect);

        // Broadcast
        broadcastExecuteText(hero);

        return SkillResult.NORMAL;
    }

    public class JabEffect extends ExpirableEffect {

        public JabEffect(Skill skill, long duration) {
            super(skill, "JabEffect", duration);
        }

        @Override
        public void applyToHero(Hero hero) {
            super.applyToHero(hero);
            Player p = hero.getPlayer();
            broadcast(p.getLocation(), SkillConfigManager.getUseSetting(hero,
                    SkillJab.this, SkillSetting.APPLY_TEXT, "").replace("$1", p.getDisplayName()));
        }

        @Override
        public void removeFromHero(Hero hero) {
            super.removeFromHero(hero);
            Player p = hero.getPlayer();
            broadcast(p.getLocation(), SkillConfigManager.getUseSetting(hero,
                    SkillJab.this, SkillSetting.EXPIRE_TEXT, "").replace("$1", p.getDisplayName()));
        }
    }

    public class SkillAssaultListener implements Listener {

        @EventHandler
        public void onWeaponDamage(WeaponDamageEvent event) {

            if (event.isCancelled()
                    || !(event.getDamager().getEntity() instanceof Player)) {
                return;
            }

            Hero hero = plugin.getCharacterManager().getHero((Player) event.getDamager().getEntity());

            if (hero.hasEffect("JabEffect")) {
                // Remove effect
                hero.removeEffect(hero.getEffect("JabEffect"));

                // Set damage to 120%
                event.setDamage(event.getDamage() * 1.2);

                // Play effect
                float particlePower = (float) SkillConfigManager.getUseSetting(hero, SkillJab.this, "particle-power", 10, false);
                int particleAmount = SkillConfigManager.getUseSetting(hero, SkillJab.this, "particle-amount", 50, false);
                Location loc = event.getEntity().getLocation();
                loc.setY(loc.getY() + 0.5);

                loc.getWorld().spigot().playEffect(loc, Effect.SMOKE, 0, 0, 0, 0, 0, particlePower, particleAmount, 64);
            }
        }
    }
}
