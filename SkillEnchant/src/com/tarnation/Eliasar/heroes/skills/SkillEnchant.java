package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;

public class SkillEnchant extends ActiveSkill {

    public SkillEnchant(Heroes plugin) {
        super(plugin, "Enchant");
        setDescription("You can enchant items with enchantments.");
        setIdentifiers("skill enchant");
        setUsage("/skill enchant [enchantment] [level]. Go to this page to find all the keywords: http://goo.gl/UTWtsC");
        setArgumentRange(2, 2);
        setTypes(SkillType.ITEM, SkillType.KNOWLEDGE);

        // Register event
        Bukkit.getServer().getPluginManager().registerEvents(new SkillEnchantListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        return node;
    }

    @Override
    public String getDescription(Hero hero) {
        String description = "";
        //String ending = "§6; ";

        // Cooldown
        /*int cooldown = (SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN.node(), 0, false)
                - SkillConfigManager.getUseSetting(hero, this, SkillSetting.COOLDOWN_REDUCE.node(), 0, false) * hero.getLevel()) / 1000;
        if (cooldown > 0) {
            description += "§6CD: §9" + cooldown + "s" + ending;
        }*/

        // Duration
        //int duration = SkillConfigManager.getUseSetting(hero, this, SkillSetting.DURATION, 10000, false);

        description += getDescription();//.replace("$1", "§9" + duration/1000 + "§6");

        return description;
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {

        // Get item in hand
        ItemStack itemInPlayerHand = hero.getPlayer().getItemInHand();
        String arg = args[0];
        int amplitude = args[1] != null ? Integer.parseInt(args[1]) : 1;
        Enchantment enchantment;
        ItemStack reagent;

        // Parse enchant argument
        if (arg.equals("protection") || arg.equals("prot")) {
            enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
        } else if (arg.equals("fireprotection") || arg.equals("fireprot")) {
            enchantment = Enchantment.PROTECTION_FIRE;
        } else if (arg.equals("featherfall") || arg.equals("fall")) {
            enchantment = Enchantment.PROTECTION_FALL;
        } else if (arg.equals("blastprotection") || arg.equals("blastprot")) {
            enchantment = Enchantment.PROTECTION_EXPLOSIONS;
        } else if (arg.equals("projectileprotection") || arg.equals("projectileprot")) {
            enchantment = Enchantment.PROTECTION_PROJECTILE;
        } else if (arg.equals("respiration") || arg.equals("breathing")) {
            enchantment = Enchantment.OXYGEN;
        } else if (arg.equals("aquaaffinity") || arg.equals("aquaaff")) {
            enchantment = Enchantment.WATER_WORKER;
        } else if (arg.equals("thorns")) {
            enchantment = Enchantment.THORNS;
        } else if (arg.equals("sharpness") || arg.equals("sharp")) {
            enchantment = Enchantment.DAMAGE_ALL;
        } else if (arg.equals("smite")) {
            enchantment = Enchantment.DAMAGE_UNDEAD;
        } else if (arg.equals("baneofarthropods") || arg.equals("bane")) {
            enchantment = Enchantment.DAMAGE_ARTHROPODS;
        } else if (arg.equals("knockback")) {
            enchantment = Enchantment.KNOCKBACK;
        } else if (arg.equals("fireaspect") || arg.equals("fire")) {
            enchantment = Enchantment.FIRE_ASPECT;
        } else if (arg.equals("looting") || arg.equals("loot")) {
            enchantment = Enchantment.LOOT_BONUS_MOBS;
        } else if (arg.equals("arrowdamage") || arg.equals("power")) {
            enchantment = Enchantment.ARROW_DAMAGE;
        } else if (arg.equals("arrowknockback") || arg.equals("punch")) {
            enchantment = Enchantment.ARROW_KNOCKBACK;
        } else if (arg.equals("arrowfire") || arg.equals("flame")) {
            enchantment = Enchantment.ARROW_FIRE;
        } else if (arg.equals("infinity")) {
            enchantment = Enchantment.ARROW_INFINITE;
        } else if (arg.equals("efficiency") || arg.equals("eff")) {
            enchantment = Enchantment.DIG_SPEED;
        } else if (arg.equals("silktouch") || arg.equals("silk")) {
            enchantment = Enchantment.SILK_TOUCH;
        } else if (arg.equals("unbreaking") || arg.equals("durability")) {
            enchantment = Enchantment.DURABILITY;
        } else if (arg.equals("fortune") || arg.equals("fort")) {
            enchantment = Enchantment.LOOT_BONUS_BLOCKS;
        } else if (arg.equals("luckofthesea") || arg.equals("luck")) {
            enchantment = Enchantment.LUCK;
        } else if (arg.equals("lure")) {
            enchantment = Enchantment.LURE;
        } else {
            broadcast(hero.getPlayer().getLocation(), arg + " is not a valid enchantment.");
            return SkillResult.FAIL;
        }

        // Check if player is appropriate level

        // Check if enchantment is valid for item in hand
        if (!enchantment.canEnchantItem(itemInPlayerHand)) {
            broadcast(hero.getPlayer().getLocation(), "You cannot enchant this item with " + enchantment.getName());
            return SkillResult.FAIL;
        }

        // If the specified amplitude is too high for a certain enchant, lower it to the max level for that enchantment
        amplitude = amplitude > enchantment.getMaxLevel() ? enchantment.getMaxLevel() : amplitude;

        // If enchantment is a certain enchant, bump up reagent cost
        int reagentAmplitude = amplitude;
        if (enchantment == Enchantment.OXYGEN
                || enchantment == Enchantment.WATER_WORKER
                || enchantment == Enchantment.KNOCKBACK
                || enchantment == Enchantment.LOOT_BONUS_MOBS
                || enchantment == Enchantment.ARROW_KNOCKBACK
                || enchantment == Enchantment.DURABILITY
                || enchantment == Enchantment.LOOT_BONUS_BLOCKS
                || enchantment == Enchantment.LUCK
                || enchantment == Enchantment.LURE) {
            reagentAmplitude += 1;
        } else if (enchantment == Enchantment.THORNS
                || enchantment == Enchantment.FIRE_ASPECT
                || enchantment == Enchantment.ARROW_FIRE) {
            reagentAmplitude += 2;
        } else if (enchantment == Enchantment.ARROW_INFINITE
                || enchantment == Enchantment.SILK_TOUCH) {
            reagentAmplitude += 3;
        }

        // Get reagent cost for amplitude
        switch (reagentAmplitude) {
            case 1:
                reagent = new ItemStack(Material.COAL_BLOCK);
                break;
            case 2:
                reagent = new ItemStack(Material.IRON_INGOT);
                break;
            case 3:
                reagent = new ItemStack(Material.GOLD_INGOT);
                break;
            case 4:
                reagent = new ItemStack(Material.EMERALD);
                break;
            case 5:
                reagent = new ItemStack(Material.DIAMOND);
                break;
            default:
                reagent = new ItemStack(Material.COAL_BLOCK);
                break;
        }

        itemInPlayerHand.addEnchantment(enchantment, amplitude);
        broadcast(hero.getPlayer().getLocation(), hero.getName() + " has enchanted the item with " + enchantment.getName() + " " + amplitude);
        return SkillResult.NORMAL;
    }

    public class SkillEnchantListener implements Listener {

        @EventHandler
        public void onPrepareItemEnchantEvent(PrepareItemEnchantEvent event) {

            if (event.isCancelled()) return;

            event.setCancelled(true);
        }
    }
}
