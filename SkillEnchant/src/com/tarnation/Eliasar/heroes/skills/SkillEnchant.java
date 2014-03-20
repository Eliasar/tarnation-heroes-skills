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
        setUsage("/skill enchant [enchantment] [level] - Check the Tarnation forums under Heroes for more information.");
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
        String friendlyEnchantName;
        int amplitude = args[1] != null ? Integer.parseInt(args[1]) : 1;
        Enchantment enchantment;
        ItemStack baseReagent;
        ItemStack extraReagent;
        int extraReagentAmplitude;

        // Parse enchant argument and set extra reagent
        if (arg.equals("protection") || arg.equals("prot")) {
            enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
            extraReagent = new ItemStack(Material.OBSIDIAN);
            friendlyEnchantName = "Protection";
        } else if (arg.equals("fireprotection") || arg.equals("fireprot")) {
            enchantment = Enchantment.PROTECTION_FIRE;
            extraReagent = new ItemStack(Material.MAGMA_CREAM);
            friendlyEnchantName = "Fire Protection";
        } else if (arg.equals("featherfall") || arg.equals("fall")) {
            enchantment = Enchantment.PROTECTION_FALL;
            extraReagent = new ItemStack(Material.FEATHER);
            friendlyEnchantName = "Feather Fall";
        } else if (arg.equals("blastprotection") || arg.equals("blastprot")) {
            enchantment = Enchantment.PROTECTION_EXPLOSIONS;
            extraReagent = new ItemStack(Material.SULPHUR);
            friendlyEnchantName = "Blast Protection";
        } else if (arg.equals("projectileprotection") || arg.equals("projectileprot")) {
            enchantment = Enchantment.PROTECTION_PROJECTILE;
            extraReagent = new ItemStack(Material.ARROW);
            friendlyEnchantName = "Projectile Protection";
        } else if (arg.equals("respiration") || arg.equals("breathing")) {
            enchantment = Enchantment.OXYGEN;
            extraReagent = new ItemStack(Material.GLASS);
            friendlyEnchantName = "Respiration";
        } else if (arg.equals("aquaaffinity") || arg.equals("aquaaff")) {
            enchantment = Enchantment.WATER_WORKER;
            extraReagent = new ItemStack(Material.RAW_FISH);
            friendlyEnchantName = "Aqua Affinity";
        } else if (arg.equals("thorns")) {
            enchantment = Enchantment.THORNS;
            extraReagent = new ItemStack(Material.CACTUS);
            friendlyEnchantName = "Thorns";
        } else if (arg.equals("sharpness") || arg.equals("sharp")) {
            enchantment = Enchantment.DAMAGE_ALL;
            extraReagent = new ItemStack(Material.FLINT);
            friendlyEnchantName = "Sharpness";
        } else if (arg.equals("smite")) {
            enchantment = Enchantment.DAMAGE_UNDEAD;
            extraReagent = new ItemStack(Material.BONE);
            friendlyEnchantName = "Smite";
        } else if (arg.equals("baneofarthropods") || arg.equals("bane")) {
            enchantment = Enchantment.DAMAGE_ARTHROPODS;
            extraReagent = new ItemStack(Material.SPIDER_EYE);
            friendlyEnchantName = "Bane of Arthropods";
        } else if (arg.equals("knockback")) {
            enchantment = Enchantment.KNOCKBACK;
            extraReagent = new ItemStack(Material.LOG);
            friendlyEnchantName = "Knockback";
        } else if (arg.equals("fireaspect") || arg.equals("fire")) {
            enchantment = Enchantment.FIRE_ASPECT;
            extraReagent = new ItemStack(Material.FLINT_AND_STEEL);
            friendlyEnchantName = "Fire Aspect";
        } else if (arg.equals("looting") || arg.equals("loot")) {
            enchantment = Enchantment.LOOT_BONUS_MOBS;
            extraReagent = new ItemStack(Material.GLOWSTONE_DUST);
            friendlyEnchantName = "Looting";
        } else if (arg.equals("arrowdamage") || arg.equals("power")) {
            enchantment = Enchantment.ARROW_DAMAGE;
            extraReagent = new ItemStack(Material.SANDSTONE);
            friendlyEnchantName = "Power";
        } else if (arg.equals("arrowknockback") || arg.equals("punch")) {
            enchantment = Enchantment.ARROW_KNOCKBACK;
            extraReagent = new ItemStack(Material.TORCH);
            friendlyEnchantName = "Punch";
        } else if (arg.equals("arrowfire") || arg.equals("flame")) {
            enchantment = Enchantment.ARROW_FIRE;
            extraReagent = new ItemStack(Material.BLAZE_POWDER);
            friendlyEnchantName = "Flame";
        } else if (arg.equals("infinity")) {
            enchantment = Enchantment.ARROW_INFINITE;
            extraReagent = new ItemStack(Material.GHAST_TEAR);
            friendlyEnchantName = "Infinity";
        } else if (arg.equals("efficiency") || arg.equals("eff")) {
            enchantment = Enchantment.DIG_SPEED;
            extraReagent = new ItemStack(Material.SUGAR);
            friendlyEnchantName = "Efficiency";
        } else if (arg.equals("silktouch") || arg.equals("silk")) {
            enchantment = Enchantment.SILK_TOUCH;
            extraReagent = new ItemStack(Material.LAPIS_BLOCK);
            friendlyEnchantName = "Silk Touch";
        } else if (arg.equals("unbreaking") || arg.equals("durability")) {
            enchantment = Enchantment.DURABILITY;
            extraReagent = new ItemStack(Material.LEATHER);
            friendlyEnchantName = "Unbreaking";
        } else if (arg.equals("fortune") || arg.equals("fort")) {
            enchantment = Enchantment.LOOT_BONUS_BLOCKS;
            extraReagent = new ItemStack(Material.CLAY_BRICK);
            friendlyEnchantName = "Fortune";
        } else if (arg.equals("luckofthesea") || arg.equals("luck")) {
            enchantment = Enchantment.LUCK;
            extraReagent = new ItemStack(Material.STRING);
            friendlyEnchantName = "Luck of the Sea";
        } else if (arg.equals("lure")) {
            enchantment = Enchantment.LURE;
            extraReagent = new ItemStack(Material.ENDER_PEARL);
            friendlyEnchantName = "Lure";
        } else {
            broadcast(hero.getPlayer().getLocation(), arg + " is not a valid enchantment.");
            return SkillResult.FAIL;
        }

        // Check if enchantment is valid for item in hand
        if (!itemInPlayerHand.equals(new ItemStack(Material.BOOK))
                || !enchantment.canEnchantItem(itemInPlayerHand)) {
            broadcast(hero.getPlayer().getLocation(), "You cannot enchant this item with " + friendlyEnchantName);
            return SkillResult.FAIL;
        }

        // If the specified amplitude is too high for a certain enchant, lower it to the max level for that enchantment
        amplitude = amplitude > enchantment.getMaxLevel() ? enchantment.getMaxLevel() : amplitude;

        // For certain enchantments, bump up reagent cost
        int reagentAmplitude = amplitude;
        if (enchantment == Enchantment.OXYGEN
                || enchantment == Enchantment.WATER_WORKER
                || enchantment == Enchantment.THORNS
                || enchantment == Enchantment.KNOCKBACK
                || enchantment == Enchantment.LOOT_BONUS_MOBS
                || enchantment == Enchantment.ARROW_KNOCKBACK
                || enchantment == Enchantment.DURABILITY
                || enchantment == Enchantment.LOOT_BONUS_BLOCKS
                || enchantment == Enchantment.LUCK
                || enchantment == Enchantment.LURE) {
            reagentAmplitude += 1;
        } else if (enchantment == Enchantment.FIRE_ASPECT
                || enchantment == Enchantment.ARROW_FIRE) {
            reagentAmplitude += 2;
        } else if (enchantment == Enchantment.ARROW_INFINITE
                || enchantment == Enchantment.SILK_TOUCH) {
            reagentAmplitude += 3;
        }

        // Set level requirement
        int levelRequirement = 1 + (reagentAmplitude - 1) * 6;
        if (hero.getLevel() < levelRequirement) {
            broadcast(hero.getPlayer().getLocation(),
                    "You must be Enchanter level " + levelRequirement + " to enchant that item with " + friendlyEnchantName + " " + amplitude + ".");
            return SkillResult.FAIL;
        }

        // Get reagent cost for amplitude
        switch (reagentAmplitude) {
            case 1:
                baseReagent = new ItemStack(Material.COAL_BLOCK);
                break;
            case 2:
                baseReagent = new ItemStack(Material.IRON_BLOCK);
                break;
            case 3:
                baseReagent = new ItemStack(Material.GOLD_BLOCK);
                break;
            case 4:
                baseReagent = new ItemStack(Material.EMERALD);
                break;
            case 5:
                baseReagent = new ItemStack(Material.DIAMOND);
                break;
            default:
                baseReagent = new ItemStack(Material.COAL_BLOCK);
                break;
        }

        // Get user-friendly item names
        String baseReagentFriendlyName = baseReagent.getItemMeta().hasDisplayName() ? baseReagent.getItemMeta().getDisplayName() : baseReagent.getType().toString();
        String extraReagentFriendlyName = extraReagent.getItemMeta().hasDisplayName() ? extraReagent.getItemMeta().getDisplayName() : extraReagent.getType().toString();

        // Set extra reagents
        extraReagentAmplitude = reagentAmplitude == 1 ? 1 : (reagentAmplitude - 1) * 5;
        extraReagent.setAmount(extraReagentAmplitude);

        // Check inventory for reagents
        if (!hero.getPlayer().getInventory().containsAtLeast(baseReagent, 1)
                || !hero.getPlayer().getInventory().containsAtLeast(extraReagent, extraReagent.getAmount())) {
            broadcast(hero.getPlayer().getLocation(),  "You must have the following reagents: 1 " + baseReagentFriendlyName + " and "
                    + extraReagent.getAmount() + " " + extraReagentFriendlyName + ".");
            return SkillResult.FAIL;
        }

        // Remove reagents and apply enchant
        hero.getPlayer().getInventory().removeItem(baseReagent);
        hero.getPlayer().getInventory().removeItem(extraReagent);

        // Apply enchant
        itemInPlayerHand.addEnchantment(enchantment, amplitude);
        broadcast(hero.getPlayer().getLocation(), hero.getName() + " has enchanted the item with " + friendlyEnchantName + " " + amplitude + "!");

        // Give exp
        double experienceGranted = ((1 + reagentAmplitude) * 5) * Math.sqrt(hero.getLevel(hero.getSecondClass()));
        hero.addExp(experienceGranted, hero.getSecondClass(), hero.getPlayer().getLocation());

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
