package com.herocraftonline.heroes.characters.skill.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.Monster;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.PeriodicDamageEffect;
import com.herocraftonline.heroes.characters.skill.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.InvocationTargetException;

public class SkillNetherTendrils extends TargettedSkill {

    public SkillNetherTendrils(Heroes plugin) {
        super(plugin, "NetherTendrils");
        setDescription("Summon nether tendrils that deal damage $1 over $2 seconds.");
        setUsage("/skill nethertendrils");
        setArgumentRange(0, 0);
        setIdentifiers("skill nethertendrils");
        setTypes(SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.DEBUFF);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.DAMAGE_TICK.node(), 3);
        node.set(SkillSetting.DAMAGE_INCREASE.node(), 0.15);
        node.set(SkillSetting.MANA.node(), 35);
        node.set(SkillSetting.COOLDOWN.node(), 20000);
        node.set(SkillSetting.PERIOD.node(), 2000);
        node.set(SkillSetting.DURATION.node(), 6000);
        node.set(SkillSetting.APPLY_TEXT.node(), "$1 has been rooted!");
        node.set(SkillSetting.EXPIRE_TEXT.node(), "$1 is no longer rooted.");
        node.set("particle-name", "portal");
        node.set("particle-power", 1);
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
        double damage = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_TICK.node(), 3, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.15, false) * hero.getLevel();

        // Period
        int period = SkillConfigManager.getUseSetting(hero, this, SkillSetting.PERIOD.node(), 2000, false) / 1000;

        // Duration
        int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 6000, false) / 1000;

        description += getDescription()
                .replace("$1", "§9" + damage * (duration/period) + "§6")
                .replace("$2", "§9" + duration + "§6");

        return description;
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] strings) {
        Player player = hero.getPlayer();
        long period = SkillConfigManager.getUseSetting(hero, this, SkillSetting.PERIOD.node(), 2000, false);
        long duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION.node(), 6000, false);
        double dmgTick = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_TICK.node(), 3, false)
                + SkillConfigManager.getUseSetting(hero, this, SkillSetting.DAMAGE_INCREASE.node(), 0.15, false) * hero.getLevel();
        NetherTendrilsEffect ne = new NetherTendrilsEffect(this, period, duration, dmgTick, player);

        // Do not damage players in creative
        if (target instanceof Player) {
            if (((Player) target).getGameMode() == GameMode.CREATIVE)
                return SkillResult.INVALID_TARGET;
        } else if (target == null) {
            return SkillResult.INVALID_TARGET;
        }

        if (Skill.damageCheck(hero.getPlayer(), target)) {
            broadcastExecuteText(hero, target);
            addSpellTarget(target, hero);
            //Skill.damageEntity(target, hero.getEntity(), dmgTick, EntityDamageEvent.DamageCause.ENTITY_ATTACK, false);
            plugin.getCharacterManager().getCharacter(target).addEffect(ne);
        } else {
            return SkillResult.INVALID_TARGET;
        }
        return SkillResult.NORMAL;
    }

    public class NetherTendrilsEffect extends PeriodicDamageEffect {

        private String applyText;
        private String expireText;
        private String particleName;
        private float particlePower;
        private int particleAmount;

        public NetherTendrilsEffect(Skill skill, long period, long duration, double tickDamage, Player caster) {
            super(skill, "NetherTendrils", period, duration, tickDamage, caster, true);
            this.types.add(EffectType.MAGIC);
            this.types.add(EffectType.DISPELLABLE);
            this.applyText = SkillConfigManager.getUseSetting(plugin.getCharacterManager().getHero(caster), SkillNetherTendrils.this, SkillSetting.APPLY_TEXT, "");
            this.expireText = SkillConfigManager.getUseSetting(plugin.getCharacterManager().getHero(caster), SkillNetherTendrils.this, SkillSetting.EXPIRE_TEXT, "");
            this.particleName = SkillConfigManager.getUseSetting(plugin.getCharacterManager().getHero(caster), SkillNetherTendrils.this, "particle-name", "");
            this.particlePower = SkillConfigManager.getUseSetting(plugin.getCharacterManager().getHero(caster),
                    SkillNetherTendrils.this, "particle-power", 1, false);
            this.particleAmount = SkillConfigManager.getUseSetting(plugin.getCharacterManager().getHero(caster),
                    SkillNetherTendrils.this, "particle-amount", 50, false);
        }

        @Override
        public void applyToHero(Hero hero) {
            super.applyToHero(hero);
            Player p = hero.getPlayer();
            broadcast(p.getLocation(), applyText.replace("$1", p.getDisplayName()));
        }

        @Override
        public void applyToMonster(Monster monster) {
            super.applyToMonster(monster);
            broadcast(monster.getEntity().getLocation(), applyText.replace("$1", monster.getName()));
        }

        @Override
        public void removeFromHero(Hero hero) {
            super.removeFromHero(hero);
            Player p = hero.getPlayer();
            broadcast(p.getLocation(), expireText.replace("$1", p.getDisplayName()));
        }

        @Override
        public void removeFromMonster(Monster monster) {
            super.removeFromMonster(monster);
            broadcast(monster.getEntity().getLocation(), expireText.replace("$1", monster.getName()));
        }

        @Override
        public void tickMonster(Monster monster) {
            //super.tickMonster(monster);
            //addSpellTarget(monster.getEntity(), plugin.getCharacterManager().getHero(getApplier()));
            damageEntity(monster.getEntity(), getApplier(), getTickDamage(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, false);

            playEffect(monster.getEntity());
        }

        @Override
        public void tickHero(Hero hero) {
            //super.tickHero(hero);
            /*Player p = hero.getPlayer();
            addSpellTarget(p, plugin.getCharacterManager().getHero(getApplier()));*/
            damageEntity(hero.getPlayer(), getApplier(), getTickDamage(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, false);

            playEffect(hero.getEntity());
        }

        public void playEffect(LivingEntity le) {

            // 0.5 blocks above location
            Location loc = le.getLocation();
            loc.setY(loc.getY() + 0.5);

            // Particle effect
            ParticleEffect pe = new ParticleEffect(
                    particleName,
                    loc,
                    particlePower,
                    particleAmount
            );
            try {
                pe.playEffect();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
