package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Random;

public class SkillWildSwing extends ActiveSkill {
    public SkillWildSwing(Heroes plugin) {
        super(plugin, "WildSwing");
        setDescription("Swing wildly for a 40% chance to do $1 damage.");
        setUsage("/skill wildswing");
        setArgumentRange(0, 0);
        setIdentifiers("skill wildswing");

        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.RADIUS.node(), 3);
        node.set(SkillSetting.RADIUS_INCREASE.node(), 0.05);
        node.set(SkillSetting.DAMAGE.node(), 6);
        node.set(SkillSetting.DAMAGE_INCREASE.node(), 0.4);
        node.set(SkillSetting.MANA.node(), 10);
        node.set(SkillSetting.COOLDOWN.node(), 5000);
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
        int healthCost = SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST, 0, false)
                - (SkillConfigManager.getUseSetting(hero, this, SkillSetting.HEALTH_COST_REDUCE, mana, true) * hero.getLevel());
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
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 6, false)
                + (SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.4, false) * hero.getLevel());

        description += getDescription().replace("$1", "§9" + damage + "§6");

        return description;
    }

    @Override
    public SkillResult use(final Hero hero, String[] args) {
        // AoE Damage
        float radius = (float) (SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS, 3, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS_INCREASE, 0.05, false));

        for (Entity e : hero.getPlayer().getNearbyEntities(radius, radius, radius)) {

            if (e instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) e;
                double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE, 6, false)
                        + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE, 0.4, false) * hero.getLevel();

                if (Skill.damageCheck(hero.getPlayer(), target)) {
                    Random random = new Random(System.currentTimeMillis());
                    if (random.nextInt(100) + 1 > 60) {
                        Skill.damageEntity(target, hero.getEntity(), damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, true);
                    }
                } else {
                    return SkillResult.INVALID_TARGET;
                }
            }
        }

        // Broadcast
        broadcastExecuteText(hero);

        return SkillResult.NORMAL;
    }
}