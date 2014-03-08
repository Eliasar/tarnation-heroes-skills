package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.Monster;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.PeriodicExpirableEffect;
import com.herocraftonline.heroes.characters.skill.*;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkillCalm extends ActiveSkill {
    public SkillCalm(Heroes plugin) {
        super(plugin, "Calm");
        setDescription("Grants $1 mana to you and your party members within $2 blocks over $3 seconds.");
        setUsage("/skill calm");
        setArgumentRange(0, 0);
        setIdentifiers("skill calm");

        setTypes(SkillType.MANA, SkillType.SILENCABLE);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.RADIUS.node(), 10);
        node.set(SkillSetting.RADIUS_INCREASE.node(), 0.1);
        node.set(SkillSetting.DURATION.node(), 8000);
        node.set(SkillSetting.DURATION_INCREASE.node(), 100);
        node.set(SkillSetting.PERIOD.node(), 2000);
        node.set(SkillSetting.AMOUNT.node(), 10);
        node.set("amount-per-level", 0.5);
        node.set(SkillSetting.COOLDOWN.node(), 10000);
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

        // Amount of mana to return
        double amount = SkillConfigManager.getUseSetting(hero, this, SkillSetting.AMOUNT.node(), 10, false)
                + SkillConfigManager.getUseSetting(hero, this, "amount-per-level", 0.5, false) * hero.getLevel();

        // Radius
        double radius = SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS.node(), 10, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.RADIUS_INCREASE.node(), 0.1, false) * hero.getLevel();

        // Duration
        int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 8000, false);

        description += getDescription()
                .replace("$1", "§9" + amount + "§6")
                .replace("$2", "§9" + radius + "§6")
                .replace("$4", "§9" + duration / 1000 + "§6");

        return description;
    }

    @Override
    public SkillResult use(final Hero hero, String[] args) {
        int period = SkillConfigManager.getUseSetting(hero, this, SkillSetting.PERIOD.node(), 2000, false);
        int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 8000, false);
        hero.addEffect(new CalmEffect(this, period, duration));

        // Broadcast
        broadcastExecuteText(hero);

        return SkillResult.NORMAL;
    }

    public class CalmEffect extends PeriodicExpirableEffect {
        public CalmEffect(Skill skill, long period, long duration) {
            super(skill, "Calm", period, duration);
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.MAGIC);
        }

        @Override
        public void tickMonster(Monster monster) {

        }

        @Override
        public void tickHero(Hero hero) {
            // Get range on player
            Player player = hero.getPlayer();
            int radius = SkillConfigManager.getUseSetting(hero, this.skill, SkillSetting.RADIUS, 10, false);
            double mana = SkillConfigManager.getUseSetting(hero, this.skill, SkillSetting.AMOUNT, 10, false)
                    + SkillConfigManager.getUseSetting(hero, this.skill, "amount-per-level", 0.5, false) * hero.getLevel();
            mana *= (double) getPeriod()/(double) getDuration();

            // Play firework effect at player location
            FireworkEffect fe = FireworkEffect.builder()
                    .withColor(Color.PURPLE)
                    .with(FireworkEffect.Type.STAR)
                    .build();
            playFirework(player.getEyeLocation(), fe);

            // Give mana to player and party members within radius
            giveMana(hero, mana);
            if (hero.hasParty()) {
                for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
                    if (e instanceof LivingEntity) {
                        LivingEntity le = (LivingEntity) e;
                        Hero chero = plugin.getCharacterManager().getHero((Player) le);
                        if (hero.getParty().isPartyMember(chero) && !chero.equals(hero)) {
                            giveMana(chero, mana);
                        }
                    }
                }
            }
        }

        // Given hero and mana, add the amount of mana to the hero
        private void giveMana(Hero hero, double mana) {
            double curMana = hero.getMana();
            double maxMana = hero.getMaxMana();
            mana = (mana + curMana <= maxMana) ? mana + curMana : maxMana;

            int setMana = (int)Math.round(mana);

            hero.setMana(setMana);
        }

        // Play firework effect
        private void playFirework(Location loc, FireworkEffect fe) {
            VisualEffect ve = new VisualEffect();
            try {
                ve.playFirework(loc.getWorld(), loc, fe);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}