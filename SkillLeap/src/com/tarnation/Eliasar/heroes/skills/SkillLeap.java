package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import com.herocraftonline.heroes.characters.skill.SkillType;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class SkillLeap extends ActiveSkill {

    public SkillLeap(Heroes plugin) {
        super(plugin, "Leap");
        setDescription("Leaps $1 blocks in the direction you are facing, dealing $2 damage when you land within $3 blocks of enemies.");
        setUsage("/skill leap");
        setArgumentRange(0, 0);
        setIdentifiers("skill leap");
        setTypes(SkillType.MOVEMENT, SkillType.DAMAGING);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.DAMAGE.node(), 2);
        node.set(SkillSetting.DAMAGE_INCREASE.node(), 0.1);
        node.set(SkillSetting.RADIUS.node(), 3);
        node.set(SkillSetting.AMOUNT.node(), 8);
        node.set(SkillSetting.MANA.node(), 10);
        node.set(SkillSetting.COOLDOWN.node(), 12000);
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
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.1, false) * hero.getLevel();

        // Distance
        int distance = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT.node(), 8, false);

        // Radius
        int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 3, false);

        description += getDescription()
                .replace("$1", "§9" + distance + "§6")
                .replace("$2", "§9" + damage + "§6")
                .replace("$3", "§9" + radius + "§6");

        return description;
    }

    @Override
    public SkillResult use(Hero hero, String[] strings) {
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 2, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.1, false) * hero.getLevel();

        int radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 3, false);

        int distance = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT.node(), 8, false);

        // Set velocity to distance
        double normalX = hero.getPlayer().getLocation().getDirection().normalize().getX() * distance;
        double normalZ = hero.getPlayer().getLocation().getDirection().normalize().getZ() * distance;

        Location targetLocation = hero.getPlayer().getLocation();
        targetLocation.setX(targetLocation.getX()
                + hero.getPlayer().getLocation().getDirection().normalize().getX() * Math.sqrt(distance));
        targetLocation.setZ(targetLocation.getZ()
                + hero.getPlayer().getLocation().getDirection().normalize().getZ() * Math.sqrt(distance));

        broadcast(hero.getPlayer().getLocation(), "[Leap] normalX = " + normalX);
        broadcast(hero.getPlayer().getLocation(), "[Leap] normalZ = " + normalZ);

        double d1 = hero.getPlayer().getLocation().getX() - targetLocation.getX();
        double d2 = hero.getPlayer().getLocation().getZ() - targetLocation.getZ();

        hero.getPlayer().setVelocity(new Vector(-d1, 2.0D, -d2));

        // Damage targets in area of radius

        return SkillResult.NORMAL;
    }

    /*@Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 1, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.1, false) * hero.getLevel();


        // Do not damage players in creative
        if (target instanceof Player) {
            if (((Player) target).getGameMode() == GameMode.CREATIVE)
                return SkillResult.INVALID_TARGET;
        }

        // Check if you can damage target
        if (Skill.damageCheck(hero.getPlayer(), target)) {
            broadcastExecuteText(hero, target);
            addSpellTarget(target, hero);

            Skill.damageEntity(target, hero.getEntity(), damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, true);
        } else {
            return SkillResult.INVALID_TARGET;
        }

        // Move caster to target location
        Location chargeLocation = target.getLocation();

        double d1 = hero.getPlayer().getLocation().getX() - chargeLocation.getX();
        double d2 = hero.getPlayer().getLocation().getZ() - chargeLocation.getZ();

        hero.getPlayer().setVelocity(new Vector(-d1, 0.2D, -d2));

        // Knock back target
        hero.getPlayer().getLocation().getDirection().normalize();
        d1 = hero.getPlayer().getLocation().getDirection().normalize().getX() * 2;
        d2 = hero.getPlayer().getLocation().getDirection().normalize().getZ() * 2;

        target.setVelocity(new Vector(d1, 0.2D, d2));

        // Create particle effect at target
        playEffect(hero, target);

        return SkillResult.NORMAL;
    }*/

    public void playEffect(Hero hero, LivingEntity target) {
        float particlePower = (float) SkillConfigManager.getUseSetting(hero, this, "particle-power", 0.5, false);
        int particleAmount = SkillConfigManager.getUseSetting(hero, this, "particle-amount", 100, false);
        Location loc = hero.getPlayer().getLocation();
        loc.setY(loc.getY() + 0.5);

        hero.getPlayer().getWorld().spigot().playEffect(loc, Effect.CLOUD, 0, 0, 0, 0, 0, particlePower, particleAmount, 64);
    }
}
