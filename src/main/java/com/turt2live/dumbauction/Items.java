package com.turt2live.dumbauction;

import org.bukkit.FireworkEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Items {
    private static Map<Integer, String> enchantmentNames = null;
    private static Map<Integer, String> enchantmentLevels = null;

    public static String[] getLore(ItemStack item) {
        if (item == null) return null;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;
        List<String> pageList = itemMeta.getLore();
        if (pageList == null) return null;
        String[] pages = new String[pageList.size()];
        for (int i = 0; i < pageList.size(); i++) {
            pages[i] = pageList.get(i);
        }
        return pages;
    }

    public static Map<Enchantment, Integer> getStoredEnchantments(ItemStack item) {
        if (item == null) return null;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta instanceof EnchantmentStorageMeta) {
            return ((EnchantmentStorageMeta) itemMeta).getStoredEnchants();
        }
        return null;
    }

    public static Integer getFireworkPower(ItemStack item) {
        if (item == null) return null;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta instanceof FireworkMeta) {
            return ((FireworkMeta) itemMeta).getPower();
        }
        return null;
    }

    public static FireworkEffect[] getFireworkEffects(ItemStack item) {
        if (item == null) return null;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta instanceof FireworkMeta) {
            List<FireworkEffect> effectList = ((FireworkMeta) itemMeta).getEffects();
            FireworkEffect[] effects = new FireworkEffect[effectList.size()];
            for (int i = 0; i < effectList.size(); i++) {
                effects[i] = effectList.get(i);
            }
            return effects;
        } else if (itemMeta instanceof FireworkEffectMeta) {
            FireworkEffect[] effects = new FireworkEffect[1];
            effects[0] = ((FireworkEffectMeta) itemMeta).getEffect();
            return effects;
        }
        return null;
    }

    public static String getHeadOwner(ItemStack item) {
        if (item == null) return null;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta instanceof SkullMeta) {
            return ((SkullMeta) itemMeta).getOwner();
        }
        return null;
    }

    public static Integer getRepairCost(ItemStack item) {
        if (item == null) return null;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta instanceof Repairable) {
            return ((Repairable) itemMeta).getRepairCost();
        }
        return null;
    }

    public static String getDisplayName(ItemStack item) {
        if (item == null) return null;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;
        return itemMeta.getDisplayName();
    }

    public static String getBookAuthor(ItemStack book) {
        if (book == null) return null;
        ItemMeta itemMeta = book.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta instanceof BookMeta) {
            return ((BookMeta) itemMeta).getAuthor();
        }
        return null;
    }

    public static String getBookTitle(ItemStack book) {
        if (book == null) return null;
        ItemMeta itemMeta = book.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta instanceof BookMeta) {
            return ((BookMeta) itemMeta).getTitle();
        }
        return null;
    }

    public static String[] getBookPages(ItemStack book) {
        if (book == null) return null;
        ItemMeta itemMeta = book.getItemMeta();
        if (itemMeta == null) return null;
        if (itemMeta instanceof BookMeta) {
            List<String> pageList = ((BookMeta) itemMeta).getPages();
            String[] pages = new String[pageList.size()];
            for (int i = 0; i < pageList.size(); i++) {
                pages[i] = pageList.get(i);
            }
            return pages;
        }
        return null;
    }

    public static String getEnchantmentName(Entry<Enchantment, Integer> enchantment) {
        int enchantmentId = enchantment.getKey().getId();
        int enchantmentLevel = enchantment.getValue();
        String enchantmentName = null;
        if (enchantmentNames == null) {
            enchantmentNames = new HashMap<Integer, String>();
            enchantmentNames.put(0, "Protection");
            enchantmentNames.put(1, "Fire Protection");
            enchantmentNames.put(2, "Feather Falling");
            enchantmentNames.put(3, "Blast Protection");
            enchantmentNames.put(4, "Projectile Protection");
            enchantmentNames.put(5, "Respiration");
            enchantmentNames.put(6, "Aqua Afinity");
            enchantmentNames.put(16, "Sharpness");
            enchantmentNames.put(17, "Smite");
            enchantmentNames.put(18, "Bane of Arthropods");
            enchantmentNames.put(19, "Knockback");
            enchantmentNames.put(20, "Fire Aspect");
            enchantmentNames.put(21, "Looting");
            enchantmentNames.put(32, "Efficiency");
            enchantmentNames.put(33, "Silk Touch");
            enchantmentNames.put(34, "Unbreaking");
            enchantmentNames.put(35, "Fortune");
            enchantmentNames.put(48, "Power");
            enchantmentNames.put(49, "Punch");
            enchantmentNames.put(50, "Flame");
            enchantmentNames.put(51, "Infinity");
        }
        if (enchantmentNames.get(enchantmentId) != null) {
            enchantmentName = enchantmentNames.get(enchantmentId) + " ";
        } else {
            enchantmentName = "UNKNOWN ";
        }
        if (enchantmentLevels == null) {
            enchantmentLevels = new HashMap<Integer, String>();
            enchantmentLevels.put(0, "");
            enchantmentLevels.put(1, "I");
            enchantmentLevels.put(2, "II");
            enchantmentLevels.put(3, "III");
            enchantmentLevels.put(4, "IV");
            enchantmentLevels.put(5, "V");
        }
        if (enchantmentLevels.get(enchantmentLevel) != null) {
            enchantmentName = enchantmentLevels.get(enchantmentLevel) + " ";
        } else {
            enchantmentName += enchantmentLevel;
        }
        return enchantmentName;
    }

}