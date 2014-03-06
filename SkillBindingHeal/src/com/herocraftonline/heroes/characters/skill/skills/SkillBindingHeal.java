package com.herocraftonline.heroes.characters.skill.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class SkillBindingHeal extends  TargettedSkill {

    public SkillBindingHeal(Heroes plugin) {
        super(plugin, "BindingHeal");
        setDescription("Heal target for $1 and then self for $2; if you have no target, then heal self for $1.");
        setUsage("/skill bindingheal");
        setArgumentRange(0, 0);
        setIdentifiers("skill bindingheal");
        setTypes(SkillType.LIGHT, SkillType.SILENCABLE);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.DAMAGE.node(), 4);
        node.set(SkillSetting.DAMAGE_INCREASE.node(), 0.2);
        node.set(SkillSetting.MAX_DISTANCE.node(), 15);
        node.set(SkillSetting.MANA.node(), 10);
        node.set(SkillSetting.COOLDOWN.node(), 4000);
        node.set("particle-name", "heart");
        node.set("particle-power", 0.5);
        node.set("particle-amount", 10);
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
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 4, false)
            + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.2, false) * hero.getLevel();

        description += getDescription().replace("$1", "§9" + damage + "§6").replace("$2", "§9" + damage * 0.5 + "§6");

        return description;
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 4, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.2, false) * hero.getLevel();
        String particleName = SkillConfigManager.getUseSetting(hero, this, "particle-name", "heart");
        float particlePower = (float) SkillConfigManager.getUseSetting(hero, this, "particle-power", 0.5, false);
        int particleAmount = SkillConfigManager.getUseSetting(hero, this, "particle-amount", 10, false);

        if (target != null && target.equals(player)) {
            // Heal target and self for half
            double curHealth = target.getHealth();
            damage = (damage + curHealth <= target.getMaxHealth()) ? damage + curHealth : target.getMaxHealth();
            target.setHealth(damage);
            ParticleEffect pe = new ParticleEffect(particleName, target.getEyeLocation(), particlePower, particleAmount);

            curHealth = player.getHealth();
            damage = ((damage * 0.5) + curHealth <= player.getMaxHealth()) ? (damage * 0.5) + curHealth : player.getMaxHealth();
            player.setHealth(damage);
            ParticleEffect pePlayer = new ParticleEffect(particleName, player.getEyeLocation(), particlePower, particleAmount);

            // Play particle effect at target
            try {
                pe.playEffect();
                pePlayer.playEffect();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            // Broadcast
            damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 4, false)
                    + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.2, false) * hero.getLevel();
            broadcast(target.getLocation(), ((Player) target).getName() + " healed for " + damage);
            broadcast(player.getLocation(), player.getName() + " healed for " + damage * 0.5);
            broadcastExecuteText(hero);

            return SkillResult.NORMAL;
        } else if (target != null && target.equals(player)) {
            // Heal self only
            double curHealth = player.getHealth();
            damage = (damage + curHealth <= player.getMaxHealth()) ? damage + curHealth : player.getMaxHealth();
            player.setHealth(damage);
            ParticleEffect pePlayer = new ParticleEffect(particleName, player.getEyeLocation(), particlePower, particleAmount);

            // Play particle effect at target
            try {
                pePlayer.playEffect();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            // Broadcast
            damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE.node(), 4, false)
                    + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.2, false) * hero.getLevel();
            broadcast(player.getLocation(), player.getName() + " healed for " + damage);
            broadcastExecuteText(hero);

            return SkillResult.NORMAL;
        } else {
            return SkillResult.INVALID_TARGET;
        }
    }
}
