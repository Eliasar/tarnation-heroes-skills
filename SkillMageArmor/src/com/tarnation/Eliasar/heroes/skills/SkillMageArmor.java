package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.WeaponDamageEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.*;
import com.tarnation.Eliasar.util.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkillMageArmor extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillMageArmor(Heroes plugin) {
        super(plugin, "MageArmor");
        setDescription("Shield yourself from $1% of an attack by reserving $1 of your maximum mana.");
        setUsage("/skill magearmor");
        setArgumentRange(0, 0);
        setIdentifiers("skill magearmor");
        setTypes(SkillType.COUNTER, SkillType.BUFF);

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillMageArmorListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(SkillSetting.AMOUNT.node(), 0.2);
        node.set(SkillSetting.MANA.node(), 20);
        node.set(SkillSetting.DURATION.node(), 1800000);
        node.set(SkillSetting.APPLY_TEXT.node(), "$1 gained Mage Armor.");
        node.set(SkillSetting.EXPIRE_TEXT.node(), "$1 lost Mage Armor.");
        node.set("particle-name", "magicCrit");
        node.set("particle-power", 0.5);
        node.set("particle-amount", 10);
        return node;
    }

    @Override
    public String getDescription(Hero hero) {
        String description = "";
        String ending = "§6; ";

        // Mana
        int mana = SkillConfigManager.getUseSetting(hero, this, SkillSetting.MANA.node(), 20, false)
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

        description += getDescription().replace("$1", "§9" + 20 + "§6");

        return description;
    }

    @Override
    public void init() {
        super.init();
        this.applyText = SkillConfigManager.getRaw(this, SkillSetting.APPLY_TEXT, "$1 gained Mage Armor.");
        this.expireText = SkillConfigManager.getRaw(this, SkillSetting.EXPIRE_TEXT, "$1 lost Mage Armor.");
    }

    @Override
    public SkillResult use(Hero hero, String[] strings) {
        if(hero.hasEffect("MageArmor")) {
            hero.removeEffect(hero.getEffect("MageArmor"));
            return SkillResult.NORMAL;
        }

        MageArmorEffect mae = new MageArmorEffect(this, SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 1800000, false));
        hero.addEffect(mae);

        // Broadcast
        broadcastExecuteText(hero);

        return SkillResult.NORMAL;
    }

    public class MageArmorEffect extends ExpirableEffect {

        public MageArmorEffect(Skill skill, long duration) {
            super(skill, "MageArmor", duration);
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.DISPELLABLE);
        }

        @Override
        public void applyToHero(Hero hero) {
            super.applyToHero(hero);
            Player p = hero.getPlayer();
            hero.addMaxMana("MageArmorManaEffect", SkillConfigManager.getUseSetting(hero, SkillMageArmor.this, SkillSetting.MANA, 20, false) * -1);
            broadcast(p.getLocation(), SkillMageArmor.this.applyText.replace("$1", p.getDisplayName()));
        }

        @Override
        public void removeFromHero(Hero hero) {
            super.removeFromHero(hero);
            Player p = hero.getPlayer();
            hero.clearMaxMana();
            hero.setMana(hero.getMana() + SkillConfigManager.getUseSetting(hero, SkillMageArmor.this, SkillSetting.MANA, 20, false));
            broadcast(p.getLocation(), SkillMageArmor.this.expireText.replace("$1", p.getDisplayName()));
        }
    }

    public class SkillMageArmorListener implements Listener {

        @EventHandler(ignoreCancelled = true)
        public void onWeaponDamage(WeaponDamageEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Player)) { return; }

            Player player = (Player) event.getEntity();
            Hero hero = plugin.getCharacterManager().getHero(player);

            if (!hero.hasEffect("MageArmor")) { return; }

            // Play particle effect
            playEffect(hero);

            double amount = SkillConfigManager.getUseSetting(hero, SkillMageArmor.this, SkillSetting.AMOUNT.node(), 0.5, false);
            amount = amount > 0 ? amount : 0;
            event.setDamage(event.getDamage() * (1.0 - amount));
        }

        @EventHandler(ignoreCancelled = true)
        public void onSkillDamage(SkillDamageEvent event) {
            if (event.isCancelled() || !(event.getEntity() instanceof Player)) { return; }

            Player player = (Player) event.getEntity();
            Hero hero = plugin.getCharacterManager().getHero(player);

            if (!hero.hasEffect("MageArmor")) { return; }

            // Play particle effect
            playEffect(hero);

            double amount = SkillConfigManager.getUseSetting(hero, SkillMageArmor.this, SkillSetting.AMOUNT.node(), 0.5, false);
            amount = amount > 0 ? amount : 0;
            event.setDamage(event.getDamage() * (1.0 - amount));
        }

        public void playEffect(Hero hero) {
            String particleName = SkillConfigManager.getUseSetting(hero, SkillMageArmor.this, "particle-name", "magicCrit");
            float particlePower = (float) SkillConfigManager.getUseSetting(hero, SkillMageArmor.this, "particle-power", 0.5, false);
            int particleAmount = SkillConfigManager.getUseSetting(hero, SkillMageArmor.this, "particle-amount", 10, false);
            ParticleEffect pe = new ParticleEffect(particleName, hero.getPlayer().getEyeLocation(), particlePower, particleAmount);
            pe.playEffect();

            // TODO: When Spigot supports it, uncomment for particles
            //CraftWorld.Spigot playerParticles = new CraftWorld.Spigot();
            //playerParticles.playEffect(player.getEyeLocation(), Effect.MAGIC_CRIT, 0, 0, 0, 0, 0, particlePower, particleAmount, 64);
            //pePlayer.playEffect();
        }
    }
}
