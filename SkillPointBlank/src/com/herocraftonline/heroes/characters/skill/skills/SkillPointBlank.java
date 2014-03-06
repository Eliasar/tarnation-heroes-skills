package com.herocraftonline.heroes.characters.skill.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.CharacterDamageManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class SkillPointBlank extends PassiveSkill {

    public SkillPointBlank(Heroes plugin) {
        super(plugin, "PointBlank");
        setDescription("Deal $1 damage if your target is within 5 blocks.");
        setIdentifiers("skill pointblank");
        setTypes(SkillType.PHYSICAL, SkillType.DAMAGING);

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillPointBlankListener(this), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.COOLDOWN.node(), 7000);
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
        /*double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 2, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.2, false) * hero.getLevel();*/
        double damage = hero.getHeroClass().getProjectileDamage(CharacterDamageManager.ProjectileType.ARROW)
                + hero.getHeroClass().getProjDamageLevel(CharacterDamageManager.ProjectileType.ARROW) * hero.getLevel();

        description += getDescription().replace("$1", "§9" + damage + "§6");

        return description;
    }

    public class SkillPointBlankListener implements Listener {

        private Skill skill;

        public SkillPointBlankListener (Skill skill) {
            this.skill = skill;
        }

        @EventHandler
        public void onWeaponDamage(WeaponDamageEvent event) {

            if (event.isCancelled() || !event.getAttackerEntity().getType().equals(EntityType.ARROW)) {
                return;
            }

            if (event.getDamager().getEntity() instanceof Player) {
                Player player = (Player) event.getDamager().getEntity();
                Hero playerHero = plugin.getCharacterManager().getHero(player);
                LivingEntity target = (LivingEntity) event.getEntity();
                Location location = target.getLocation();

                Map<String, Long> cooldowns = playerHero.getCooldowns();

                if (location.distance(player.getLocation()) <= 5) {
                    if (!cooldowns.containsKey("pointblank")) {
                        // First time to gain CD
                        if (target instanceof Player) {
                            broadcast(player.getLocation(), player.getName() + " has shot " + ((Player) target).getName() + " point blank!");
                        } else {
                            broadcast(player.getLocation(), player.getName() + " has shot " + target.getType() + " point blank!");
                        }

                        event.setDamage(event.getDamage() * 1.5);

                        playerHero.setCooldown(
                                skill.getName(),
                                System.currentTimeMillis() + SkillConfigManager.getUseSetting(playerHero, skill, SkillSetting.COOLDOWN, 7000, false)
                        );
                    } else if ((playerHero.getCooldown("pointblank") - System.currentTimeMillis()) <= 0) {
                        broadcast(player.getLocation(), player.getName() + " has shot " + target.getCustomName() + " point blank!");
                        event.setDamage(event.getDamage() * 1.5);

                        playerHero.setCooldown(
                                skill.getName(),
                                System.currentTimeMillis() + SkillConfigManager.getUseSetting(playerHero, skill, SkillSetting.COOLDOWN, 7000, false)
                        );
                    }
                }
            }
        }
    }
}
