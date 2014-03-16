package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.common.SafeFallEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import com.herocraftonline.heroes.characters.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class SkillLeap extends ActiveSkill {

    public SkillLeap(Heroes plugin) {
        super(plugin, "Leap");
        setDescription("Leaps $1 blocks in the direction you are facing.");
        setUsage("/skill leap");
        setArgumentRange(0, 0);
        setIdentifiers("skill leap");
        setTypes(SkillType.MOVEMENT, SkillType.DAMAGING);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
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

        // Distance
        int distance = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT.node(), 8, false);

        description += getDescription()
                .replace("$1", "§9" + distance + "§6");

        return description;
    }

    @Override
    public SkillResult use(Hero hero, String[] strings) {

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

        // Give safefall
        hero.addEffect(new SafeFallEffect(this, 4000));

        return SkillResult.NORMAL;
    }
}
