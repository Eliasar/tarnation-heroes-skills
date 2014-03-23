package com.tarnation.Eliasar.heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

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

        description += getDescription();

        return description;
    }

    @Override
    public SkillResult use(Hero hero, String[] args) {

        // Get item in hand
        ItemStack itemInPlayerHand = hero.getPlayer().getItemInHand();
        String arg = args[0];
        FriendlyEnchantmentName friendlyEnchantName;
        int amplitude = args[1] != null ? Integer.parseInt(args[1]) : 1;
        Enchantment enchantment;
        ItemStack baseReagent;
        ItemStack extraReagent;
        int extraReagentAmplitude;

        // Parse enchant argument and set extra reagent
        if (arg.equals("protection") || arg.equals("prot")) {
            enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
            extraReagent = new ItemStack(Material.OBSIDIAN);
        } else if (arg.equals("fireprotection") || arg.equals("fireprot")) {
            enchantment = Enchantment.PROTECTION_FIRE;
            extraReagent = new ItemStack(Material.MAGMA_CREAM);
        } else if (arg.equals("featherfall") || arg.equals("fall")) {
            enchantment = Enchantment.PROTECTION_FALL;
            extraReagent = new ItemStack(Material.FEATHER);
        } else if (arg.equals("blastprotection") || arg.equals("blastprot")) {
            enchantment = Enchantment.PROTECTION_EXPLOSIONS;
            extraReagent = new ItemStack(Material.SULPHUR);
        } else if (arg.equals("projectileprotection") || arg.equals("projectileprot")) {
            enchantment = Enchantment.PROTECTION_PROJECTILE;
            extraReagent = new ItemStack(Material.ARROW);
        } else if (arg.equals("respiration") || arg.equals("breathing")) {
            enchantment = Enchantment.OXYGEN;
            extraReagent = new ItemStack(Material.GLASS);
        } else if (arg.equals("aquaaffinity") || arg.equals("aquaaff")) {
            enchantment = Enchantment.WATER_WORKER;
            extraReagent = new ItemStack(Material.RAW_FISH);
        } else if (arg.equals("thorns")) {
            enchantment = Enchantment.THORNS;
            extraReagent = new ItemStack(Material.CACTUS);
        } else if (arg.equals("sharpness") || arg.equals("sharp")) {
            enchantment = Enchantment.DAMAGE_ALL;
            extraReagent = new ItemStack(Material.FLINT);
        } else if (arg.equals("smite")) {
            enchantment = Enchantment.DAMAGE_UNDEAD;
            extraReagent = new ItemStack(Material.BONE);
        } else if (arg.equals("baneofarthropods") || arg.equals("bane")) {
            enchantment = Enchantment.DAMAGE_ARTHROPODS;
            extraReagent = new ItemStack(Material.SPIDER_EYE);
        } else if (arg.equals("knockback")) {
            enchantment = Enchantment.KNOCKBACK;
            extraReagent = new ItemStack(Material.LOG);
        } else if (arg.equals("fireaspect") || arg.equals("fire")) {
            enchantment = Enchantment.FIRE_ASPECT;
            extraReagent = new ItemStack(Material.FLINT_AND_STEEL);
        } else if (arg.equals("looting") || arg.equals("loot")) {
            enchantment = Enchantment.LOOT_BONUS_MOBS;
            extraReagent = new ItemStack(Material.GLOWSTONE_DUST);
        } else if (arg.equals("arrowdamage") || arg.equals("power")) {
            enchantment = Enchantment.ARROW_DAMAGE;
            extraReagent = new ItemStack(Material.SANDSTONE);
        } else if (arg.equals("arrowknockback") || arg.equals("punch")) {
            enchantment = Enchantment.ARROW_KNOCKBACK;
            extraReagent = new ItemStack(Material.TORCH);
        } else if (arg.equals("arrowfire") || arg.equals("flame")) {
            enchantment = Enchantment.ARROW_FIRE;
            extraReagent = new ItemStack(Material.BLAZE_POWDER);
        } else if (arg.equals("infinity")) {
            enchantment = Enchantment.ARROW_INFINITE;
            extraReagent = new ItemStack(Material.GHAST_TEAR);
        } else if (arg.equals("efficiency") || arg.equals("eff")) {
            enchantment = Enchantment.DIG_SPEED;
            extraReagent = new ItemStack(Material.SUGAR);
        } else if (arg.equals("silktouch") || arg.equals("silk")) {
            enchantment = Enchantment.SILK_TOUCH;
            extraReagent = new ItemStack(Material.LAPIS_BLOCK);
        } else if (arg.equals("unbreaking") || arg.equals("durability")) {
            enchantment = Enchantment.DURABILITY;
            extraReagent = new ItemStack(Material.LEATHER);
        } else if (arg.equals("fortune") || arg.equals("fort")) {
            enchantment = Enchantment.LOOT_BONUS_BLOCKS;
            extraReagent = new ItemStack(Material.CLAY_BRICK);
        } else if (arg.equals("luckofthesea") || arg.equals("luck")) {
            enchantment = Enchantment.LUCK;
            extraReagent = new ItemStack(Material.STRING);
        } else if (arg.equals("lure")) {
            enchantment = Enchantment.LURE;
            extraReagent = new ItemStack(Material.ENDER_PEARL);
        } else {
            hero.getPlayer().sendMessage(ChatColor.GRAY + arg + " is not a valid enchantment.");
            return SkillResult.FAIL;
        }

        // Get friendly enchantment name
        friendlyEnchantName = FriendlyEnchantmentName.valueOf(enchantment.getName());

        // Check if enchantment is valid for item in hand
        if (!enchantment.canEnchantItem(itemInPlayerHand) && !itemInPlayerHand.equals(new ItemStack(Material.BOOK))) {
            hero.getPlayer().sendMessage(ChatColor.GRAY + "You cannot enchant this item with " + friendlyEnchantName.getCode());
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
            hero.getPlayer().sendMessage(ChatColor.GRAY
                    + "You must be Enchanter level " + levelRequirement + " to enchant that item with "
                    + friendlyEnchantName.getCode() + " " + amplitude + ".");
            return SkillResult.FAIL;
        }

        // Get reagent cost for amplitude
        switch (reagentAmplitude) {
            case 1:
                baseReagent = new ItemStack(Material.COAL);
                break;
            case 2:
                baseReagent = new ItemStack(Material.IRON_INGOT);
                break;
            case 3:
                baseReagent = new ItemStack(Material.GOLD_INGOT);
                break;
            case 4:
                baseReagent = new ItemStack(Material.EMERALD);
                break;
            case 5:
                baseReagent = new ItemStack(Material.DIAMOND);
                break;
            default:
                baseReagent = new ItemStack(Material.COAL);
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
            hero.getPlayer().sendMessage(ChatColor.GRAY + "You must have the following reagents: 1 " + baseReagentFriendlyName + " and "
                    + extraReagent.getAmount() + " " + extraReagentFriendlyName + ".");
            return SkillResult.FAIL;
        }

        // Remove reagents and apply enchant
        hero.getPlayer().getInventory().removeItem(baseReagent);
        hero.getPlayer().getInventory().removeItem(extraReagent);

        // Apply enchant
        if (itemInPlayerHand.equals(new ItemStack(Material.BOOK))) {
            // Remove 1 book
            hero.getPlayer().getInventory().removeItem(new ItemStack(Material.BOOK));

            // Replace with enchant book of enchant
            ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta)enchantedBook.getItemMeta();
            esm.addStoredEnchant(enchantment, amplitude, true);
            enchantedBook.setItemMeta(esm);
            hero.getPlayer().getInventory().setItemInHand(enchantedBook);
        } else if (itemInPlayerHand.getType().equals(Material.ENCHANTED_BOOK)) {
            // Add enchant to book
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta)itemInPlayerHand.getItemMeta();
            esm.addStoredEnchant(enchantment, amplitude, true);
            itemInPlayerHand.setItemMeta(esm);
        } else {
            // Enchant item
            itemInPlayerHand.addEnchantment(enchantment, amplitude);
            broadcast(hero.getPlayer().getLocation(), hero.getName() + " has enchanted the item with " + friendlyEnchantName + " " + amplitude + "!");
        }

        // Give exp
        double experienceGranted = ((1 + reagentAmplitude) * 5) * Math.sqrt(hero.getLevel(hero.getSecondClass()));
        hero.addExp(experienceGranted, hero.getSecondClass(), hero.getPlayer().getLocation());

        return SkillResult.NORMAL;
    }

    // Enum for friendly enchantment names
    @SuppressWarnings("unused")
    public enum FriendlyEnchantmentName {
        PROTECTION_ENVIRONMENTAL("Protection"),
        PROTECTION_FIRE("Fire Protection"),
        PROTECTION_FALL("Feather Fall"),
        PROTECTION_EXPLOSIONS("Blast Protection"),
        PROTECTION_PROJECTILE("Projectile Protection"),
        OXYGEN("Respiration"),
        WATER_WORKER("Aqua Affinity"),
        THORNS("Thorns"),
        DAMAGE_ALL("Sharpness"),
        DAMAGE_UNDEAD("Smite"),
        DAMAGE_ARTHROPODS("Bane of Arthropods"),
        KNOCKBACK("Knockback"),
        FIRE_ASPECT("Fire Aspect"),
        LOOT_BONUS_MOBS("Looting"),
        ARROW_DAMAGE("Power"),
        ARROW_KNOCKBACK("Punch"),
        ARROW_FIRE("Flame"),
        ARROW_INFINITE("Infinity"),
        DIG_SPEED("Efficiency"),
        SILK_TOUCH("Silk Touch"),
        DURABILITY("Unbreaking"),
        LOOT_BONUS_BLOCKS("Fortune"),
        LUCK("Luck of the Sea"),
        LURE("Lure");
        private String code;

        private FriendlyEnchantmentName(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public class SkillEnchantListener implements Listener {

        private ItemStack rightClickItem;

        // Cancel use of Enchantment Table
        @EventHandler(priority= EventPriority.LOWEST)
        public void onPlayerInteract(PlayerInteractEvent event) {

            if (event.isCancelled()) return;

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                    && event.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE)) {

                event.setCancelled(true);
            }
        }

        // Clear rightClickItem when inventory opens
        @EventHandler
        public void onInventoryCloseEvent(InventoryCloseEvent event) {

            if (rightClickItem != null) ((Player)event.getPlayer()).sendMessage("Enchant cancelled");

            rightClickItem = null;
        }

        @EventHandler
        public void onInventoryClickEvent(InventoryClickEvent event) {

            if (event.isCancelled()) return;

            Hero hero = plugin.getCharacterManager().getHero((Player)event.getWhoClicked());

            // Event is a right click on an enchanted book
            if (event.isRightClick()) {
                //hero.getPlayer().sendMessage(ChatColor.GRAY + "You right clicked an object.");
                if (event.getCurrentItem().getType().equals(Material.ENCHANTED_BOOK)) {
                    rightClickItem = event.getCurrentItem();
                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta)rightClickItem.getItemMeta();
                    String message = ChatColor.GRAY + "";
                    for (Map.Entry<Enchantment, Integer> e : esm.getStoredEnchants().entrySet()) {
                        FriendlyEnchantmentName friendlyEnchantmentName = FriendlyEnchantmentName.valueOf(e.getKey().getName());
                        message += friendlyEnchantmentName.getCode() + ":" + e.getValue() + ", ";
                    }
                    hero.getPlayer().sendMessage(ChatColor.WHITE + "Trying to enchant with: " + message.substring(0, message.length() - 2));
                }
            } else if (event.isLeftClick()) {
                //hero.getPlayer().sendMessage(ChatColor.GRAY + "You left clicked an object.");
                if (rightClickItem != null && !event.getCurrentItem().getType().equals(Material.AIR)) {
                    ItemStack leftClickItem = event.getCurrentItem();
                    EnchantmentStorageMeta rightClickItemESM = (EnchantmentStorageMeta)rightClickItem.getItemMeta();
                    for (Map.Entry<Enchantment, Integer> e : rightClickItemESM.getStoredEnchants().entrySet()) {
                        //hero.getPlayer().sendMessage(ChatColor.GRAY + "" + e.getKey() + ":" + e.getValue());
                        if (e.getKey().canEnchantItem(leftClickItem)) {
                            FriendlyEnchantmentName friendlyEnchantmentName = FriendlyEnchantmentName.valueOf(e.getKey().getName());
                            leftClickItem.addEnchantment(e.getKey(), e.getValue());
                            broadcast(hero.getPlayer().getLocation(), hero.getName() + " has enchanted the item with "
                                    + friendlyEnchantmentName.getCode() + " " + e.getValue() + "!");
                        } else {
                            FriendlyEnchantmentName friendlyEnchantmentName = FriendlyEnchantmentName.valueOf(e.getKey().getName());
                            hero.getPlayer().sendMessage(ChatColor.RED + "You cannot enchant that item with "
                                    + friendlyEnchantmentName.getCode() + " " + e.getValue() + " :(");
                        }
                    }

                    // Destroy book, reset right click item, cancel left click event
                    final Hero finalHero = hero;
                    final ItemStack finalRightClickItem = rightClickItem;
                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            finalHero.getPlayer().getInventory().removeItem(finalRightClickItem);
                        }
                    });
                    //hero.getPlayer().getInventory().removeItem(event.getCursor());
                    hero.getPlayer().sendMessage("LeftClickItem = " + leftClickItem.getType());
                    hero.getPlayer().sendMessage("rightClickItem = " + rightClickItem.getType());
                    //hero.getPlayer().getInventory().removeItem(rightClickItem);
                    rightClickItem = null;
                    //event.setCancelled(true);
                } else {
                    // Left clicked on air
                    rightClickItem = null;
                }
            }
        }
    }
}
