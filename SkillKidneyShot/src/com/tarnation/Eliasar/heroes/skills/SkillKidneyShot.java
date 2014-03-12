package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.common.StunEffect;
import com.herocraftonline.heroes.characters.skill.*;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class SkillKidneyShot extends TargettedSkill {

    public SkillKidneyShot(Heroes plugin) {
        super(plugin, "KidneyShot");
        setDescription("Stuns your target for $1 seconds and deals $2 damage with a quick shot to the kidney.");
        setUsage("/skill kidneyshot");
        setArgumentRange(0, 0);
        setIdentifiers("skill kidneyshot");
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING, SkillType.MOVEMENT, SkillType.DEBUFF);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.DAMAGE.node(), 2);
        node.set(SkillSetting.DAMAGE_INCREASE.node(), 0.2);
        node.set(SkillSetting.MAX_DISTANCE.node(), 3);
        node.set(SkillSetting.DURATION.node(), 3000);
        node.set(SkillSetting.MANA.node(), 20);
        node.set(SkillSetting.COOLDOWN.node(), 30000);
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

        // Duration
        int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 3000, false) / 1000;

        // Damage
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 2, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.2, false) * hero.getLevel();

        description += getDescription()
                .replace("$1", "§9" + duration + "§6")
                .replace("$2", "§9" + damage + "§6");

        return description;
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 2, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.2, false) * hero.getLevel();
        int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 3, false);


        // Do not damage players in creative
        if (target instanceof Player) {
            if (((Player) target).getGameMode() == GameMode.CREATIVE)
                return SkillResult.INVALID_TARGET;
        }

        // Check if you can damage target
        if (Skill.damageCheck(hero.getPlayer(), target)) {
            broadcastExecuteText(hero, target);
            addSpellTarget(target, hero);

            // Deal damage
            Skill.damageEntity(target, hero.getEntity(), damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, true);

            // Stun
            plugin.getCharacterManager().getCharacter(target).addEffect(
                    new StunEffect(SkillKidneyShot.this, duration)
            );
        } else {
            return SkillResult.INVALID_TARGET;
        }

        // Create particle effect at target
        playEffect(hero);

        return SkillResult.NORMAL;
    }

    private void playEffect(Hero hero) {
        float particlePower = (float) SkillConfigManager.getUseSetting(hero, this, "particle-power", 0.5, false);
        int particleAmount = SkillConfigManager.getUseSetting(hero, this, "particle-amount", 100, false);
        Location loc = hero.getPlayer().getLocation();
        loc.setY(loc.getY() + 0.5);

        hero.getPlayer().getWorld().spigot().playEffect(loc, Effect.FLYING_GLYPH, 0, 0, 0, 0, 0, particlePower, particleAmount, 64);
    }
}
