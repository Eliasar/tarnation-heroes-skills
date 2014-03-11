package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.common.QuickenEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillSetting;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SkillSprint extends ActiveSkill {

    public SkillSprint(Heroes plugin) {
        super(plugin, "Sprint");
        setDescription("You sprint twice as fast for $1 seconds.");
        setIdentifiers("skill sprint");
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.DURATION.node(), 10000);
        node.set(SkillSetting.APPLY_TEXT.node(), "$1 gains Sprint.");
        node.set(SkillSetting.EXPIRE_TEXT.node(), "$1 loses Sprint.");
        node.set("particle-power", 1);
        node.set("particle-amount", 1);
        return node;
    }

    @Override
    public String getDescription(Hero hero) {
        String description = "";
        String ending = "§6; ";

        // Cooldown
        int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false)
                - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getLevel()) / 1000;
        if (cooldown > 0) {
            description += "§6CD: §9" + cooldown + "s" + ending;
        }

        // Duration
        int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 10000, false);

        description += getDescription().replace("$1", "§9" + duration + "§6");

        return description;
    }

    @Override
    public SkillResult use(Hero hero, String[] strings) {
        SprintEffect sprintEffect = new SprintEffect(
                this,
                SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 10000, false),
                2,
                SkillConfigManager.getUseSetting(hero, this, SkillSetting.APPLY_TEXT, ""),
                SkillConfigManager.getUseSetting(hero, this, SkillSetting.EXPIRE_TEXT, ""));
        hero.addEffect(sprintEffect);

        // Broadcast
        broadcastExecuteText(hero);

        return SkillResult.NORMAL;
    }

    public class SprintEffect extends QuickenEffect {

        private int particleEffectTaskID;

        public SprintEffect(Skill skill, long duration, int amplifier, String applyText, String expireText) {
            super(skill, "SprintEffect", duration, amplifier, applyText, expireText);
        }


        @Override
        public void applyToHero(Hero hero) {
            super.applyToHero(hero);
            Player p = hero.getPlayer();
            final Hero finalHero = hero;
            broadcast(p.getLocation(), SkillConfigManager.getUseSetting(hero,
                    SkillSprint.this, SkillSetting.APPLY_TEXT, "").replace("$1", p.getDisplayName()));
            particleEffectTaskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
                    plugin,
                    new Runnable() {
                        @Override
                        public void run() {
                            playEffect(finalHero);
                        }
                    },
                    0L,
                    5L
            );
        }

        @Override
        public void removeFromHero(Hero hero) {
            super.removeFromHero(hero);
            Player p = hero.getPlayer();
            broadcast(p.getLocation(), SkillConfigManager.getUseSetting(hero,
                    SkillSprint.this, SkillSetting.EXPIRE_TEXT, "").replace("$1", p.getDisplayName()));
            plugin.getServer().getScheduler().cancelTask(particleEffectTaskID);
        }

        public void playEffect(Hero hero) {
            float particlePower = (float) SkillConfigManager.getUseSetting(hero, SkillSprint.this, "particle-power", 1, false);
            int particleAmount = SkillConfigManager.getUseSetting(hero, SkillSprint.this, "particle-amount", 1, false);
            Location loc = hero.getPlayer().getLocation();
            loc.setY(loc.getY() + 0.5);

            loc.getWorld().spigot().playEffect(loc, Effect.HAPPY_VILLAGER, 0, 0, 0, 0, 0, particlePower, particleAmount, 64);
        }
    }
}
