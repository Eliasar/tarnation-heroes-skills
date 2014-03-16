package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.*;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class SkillHolyWords extends TargettedSkill {

    public SkillHolyWords(Heroes plugin) {
        super(plugin, "HolyWords");
        setDescription("Deal $1 damage to your target. Deals $2 to undead.");
        setUsage("/skill holywords");
        setArgumentRange(0, 0);
        setIdentifiers("skill holywords");
        setTypes(SkillType.LIGHT, SkillType.SILENCABLE, SkillType.DAMAGING);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.DAMAGE.node(), 2);
        node.set(SkillSetting.DAMAGE_INCREASE.node(), 0.2);
        node.set(SkillSetting.MAX_DISTANCE.node(), 15);
        node.set(SkillSetting.MANA.node(), 10);
        node.set(SkillSetting.COOLDOWN.node(), 2500);
        node.set("particle-power", 0.5);
        node.set("particle-amount", 100);
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

        // Damage
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 2, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.2, false) * hero.getLevel();

        description += getDescription()
                .replace("$1", "§9" + damage + "§6")
                .replace("$2", "§9" + damage*2 + "§6");

        return description;
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 2, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.2, false) * hero.getLevel();


        // Do not damage players in creative
        if (target instanceof Player) {
            if (((Player) target).getGameMode() == GameMode.CREATIVE)
                return SkillResult.INVALID_TARGET;
        }

        // Check if you can damage target
        if (Skill.damageCheck(hero.getPlayer(), target)) {
            broadcastExecuteText(hero, target);
            addSpellTarget(target, hero);

            // Double damage for Zombies and Skeletons
            if (target.getType() == EntityType.ZOMBIE || target.getType() == EntityType.SKELETON)
                damage *= 2.0;

            Skill.damageEntity(target, hero.getEntity(), damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, true);
        } else {
            return SkillResult.INVALID_TARGET;
        }

        // Create particle effect at target
        playEffect(hero, target);

        return SkillResult.NORMAL;
    }

    private void playEffect(Hero hero, LivingEntity target) {
        float particlePower = (float) SkillConfigManager.getUseSetting(hero, this, "particle-power", 0.5, false);
        int particleAmount = SkillConfigManager.getUseSetting(hero, this, "particle-amount", 100, false);
        Location loc = target.getLocation();
        loc.setY(loc.getY() + 0.5);

        hero.getPlayer().getWorld().spigot().playEffect(loc, Effect.FLYING_GLYPH, 0, 0, 0, 0, 0, particlePower, particleAmount, 64);
    }
}
