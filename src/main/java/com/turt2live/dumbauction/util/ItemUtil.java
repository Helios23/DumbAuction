package com.turt2live.dumbauction.util;

import com.turt2live.dumbauction.DumbAuction;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.Colorable;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Item utility methods
 *
 * @author turt2live
 */
public class ItemUtil {

    /**
     * Counts how many of an item stack an inventory contains
     *
     * @param template  the template to use, amount is ignored
     * @param inventory the inventory to check
     * @return the total number of items this inventory contains
     */
    public static int getCount(ItemStack template, Inventory inventory) {
        if (inventory == null) throw new IllegalArgumentException();
        int count = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && template.isSimilar(stack)) count += stack.getAmount();
        }
        return count;
    }

    /**
     * Sends a display of item information to the specified command sender. This is used for sending
     * the current auction queue to a command sender and therefore the resulting text is a "short hand"
     * version.
     *
     * @param item      the item to display, cannot be null
     * @param sender    the command sender to send the display to, cannot be null
     * @param placement the number to prefix the message. If less than zero, nothing is appended
     */
    public static void showQuickInformation(ItemStack item, CommandSender sender, int placement) {
        if (item == null || sender == null) throw new IllegalArgumentException();

        StringBuilder builder = new StringBuilder();
        builder.append(getName(item)).append(" ");
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            builder.append(ChatColor.ITALIC).append("(").append(getName(item, true)).append(") ");
        }
        builder.append(ChatColor.BLUE).append("x").append(item.getAmount());

        DumbAuction.getInstance().sendMessage(sender, ChatColor.GREEN + (placement > 0 ? "#" + placement : placement == 0 ? "(current)" : "") + " " + ChatColor.AQUA + builder.toString().trim());
    }

    /**
     * Sends a display of item information to the specified command sender
     *
     * @param item   the item to display, cannot be null
     * @param sender the command sender to send the display to, cannot be null
     */
    public static void showInformation(ItemStack item, CommandSender sender) {
        if (item == null || sender == null) throw new IllegalArgumentException();

        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.BOLD).append(getName(item)).append(" ");
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            builder.append(ChatColor.ITALIC).append("(").append(getName(item, true)).append(") ");
        }
        builder.append(ChatColor.BLUE).append("x").append(item.getAmount());
        DumbAuction.getInstance().sendMessage(sender, ChatColor.AQUA + builder.toString().trim());

        // Display durability
        if (item.getType().getMaxDurability() > 0 && item.getDurability() != 0) {
            double durability = 1 - ((double) item.getDurability() / (double) item.getType().getMaxDurability());
            int percent = (int) Math.round(durability * 100);
            DumbAuction.getInstance().sendMessage(sender, ChatColor.GRAY + "Durability: " + ChatColor.AQUA + "" + percent + "%");
        }

        Potion pot = null;
        try {
            pot = Potion.fromItemStack(item);
        } catch (Exception e) {
        } // Consume error
        List<String> metaMessage = new ArrayList<String>();
        if (item.hasItemMeta() || pot != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                metaMessage.add(ChatColor.LIGHT_PURPLE + "Lore: ");
                metaMessage.add(ChatColor.DARK_GRAY + "-------------");
                for (String l : lore) {
                    metaMessage.add(ChatColor.GRAY + l);
                }
                metaMessage.add(ChatColor.DARK_GRAY + "-------------");
            }
            if (meta.hasEnchants() || meta instanceof EnchantmentStorageMeta) {
                metaMessage.add(ChatColor.LIGHT_PURPLE + "Enchants: ");
                metaMessage.add(ChatColor.DARK_GRAY + "-------------");
                if (meta.hasEnchants()) {
                    Map<Enchantment, Integer> enchants = meta.getEnchants();
                    for (Enchantment e : enchants.keySet()) {
                        int level = enchants.get(e);
                        String strLevel = integerToRomanNumeral(level) + " " + ChatColor.GRAY + "(" + level + ")";
                        metaMessage.add(ChatColor.AQUA + getEnchantmentName(e) + " " + strLevel);
                    }
                }
                if (meta instanceof EnchantmentStorageMeta) {
                    EnchantmentStorageMeta emeta = (EnchantmentStorageMeta) meta;
                    if (emeta.hasStoredEnchants()) {
                        Map<Enchantment, Integer> enchants = emeta.getStoredEnchants();
                        for (Enchantment e : enchants.keySet()) {
                            int level = enchants.get(e);
                            String strLevel = integerToRomanNumeral(level) + " " + ChatColor.GRAY + "(" + level + ")";
                            metaMessage.add(ChatColor.AQUA + getEnchantmentName(e) + " " + strLevel);
                        }
                    }
                }
                metaMessage.add(ChatColor.DARK_GRAY + "-------------");
            }
            if (meta instanceof BookMeta) {
                BookMeta book = (BookMeta) meta;
                if (book.hasTitle())
                    metaMessage.add(ChatColor.GRAY + "Book Title: " + ChatColor.AQUA + book.getTitle());
                if (book.hasAuthor())
                    metaMessage.add(ChatColor.GRAY + "Book Author: " + ChatColor.AQUA + book.getAuthor());
            }
            List<FireworkEffect> effects = new ArrayList<FireworkEffect>();
            int fireworkPower = -1;
            if (meta instanceof FireworkEffectMeta) {
                FireworkEffectMeta firework = (FireworkEffectMeta) meta;
                if (firework.hasEffect()) {
                    effects.add(firework.getEffect());
                }
            }
            if (meta instanceof FireworkMeta) {
                FireworkMeta firework = (FireworkMeta) meta;
                if (firework.hasEffects()) {
                    effects.addAll(firework.getEffects());
                }
                fireworkPower = firework.getPower();
            }
            if (effects.size() > 0) {
                metaMessage.add(ChatColor.LIGHT_PURPLE + "Firework Effects: ");
                metaMessage.add(ChatColor.DARK_GRAY + "-------------");
                for (FireworkEffect effect : effects) {
                    metaMessage.add(ChatColor.AQUA + getFireworkTypeName(effect.getType()));
                    if (effect.getColors().size() > 0) {
                        builder = new StringBuilder();
                        for (Color color : effect.getColors()) {
                            ChatColor chat = ChatColorPalette.matchColor(color.getRed(), color.getGreen(), color.getBlue());
                            String name = getChatName(chat);
                            builder.append(chat).append(name).append(ChatColor.GRAY).append(", ");
                        }
                        metaMessage.add(ChatColor.GRAY + "    Colors: " + builder.toString().substring(0, builder.toString().length() - 2));
                    }
                    if (effect.getFadeColors().size() > 0) {
                        builder = new StringBuilder();
                        for (Color color : effect.getFadeColors()) {
                            ChatColor chat = ChatColorPalette.matchColor(color.getRed(), color.getGreen(), color.getBlue());
                            String name = getChatName(chat);
                            builder.append(chat).append(name).append(ChatColor.GRAY).append(", ");
                        }
                        metaMessage.add(ChatColor.GRAY + "    Fade Colors: " + builder.toString().substring(0, builder.toString().length() - 2));
                    }
                }
                metaMessage.add(ChatColor.DARK_GRAY + "-------------");
            }
            if (fireworkPower >= 0) {
                metaMessage.add(ChatColor.GRAY + "Firework Power: " + ChatColor.AQUA + "" + fireworkPower);
            }
            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta leather = (LeatherArmorMeta) meta;
                if (!leather.getColor().equals(DumbAuction.getInstance().getServer().getItemFactory().getDefaultLeatherColor())) {
                    ChatColor chat = ChatColorPalette.matchColor(leather.getColor().getRed(), leather.getColor().getGreen(), leather.getColor().getBlue());
                    metaMessage.add(ChatColor.GRAY + "Leather Color: " + chat + getChatName(chat));
                }
            }
            if (meta instanceof SkullMeta) {
                SkullMeta skull = (SkullMeta) meta;
                if (skull.hasOwner()) {
                    metaMessage.add(ChatColor.GRAY + "Skull Player: " + ChatColor.AQUA + skull.getOwner());
                }
            }
            if (meta instanceof PotionMeta || pot != null) {
                metaMessage.add(ChatColor.LIGHT_PURPLE + "Potion Effects: ");
                metaMessage.add(ChatColor.DARK_GRAY + "-------------");
                if (pot != null) {
                    for (PotionEffect effect : pot.getEffects()) {
                        int amplifier = effect.getAmplifier() + 1;
                        int time = effect.getDuration() / 20;
                        metaMessage.add(ChatColor.AQUA + getPotionEffectName(effect.getType()) + " " + integerToRomanNumeral(amplifier) + " " + ChatColor.GRAY + "(" + amplifier + ") for " + ChatColor.AQUA + toTime(time));
                    }
                }
                if (meta instanceof PotionMeta) {
                    PotionMeta potion = (PotionMeta) meta;
                    if (potion.hasCustomEffects()) {
                        for (PotionEffect effect : potion.getCustomEffects()) {
                            int amplifier = effect.getAmplifier() + 1;
                            int time = effect.getDuration() / 20;
                            metaMessage.add(ChatColor.AQUA + getPotionEffectName(effect.getType()) + " " + integerToRomanNumeral(amplifier) + " " + ChatColor.GRAY + "(" + amplifier + ") for " + ChatColor.AQUA + toTime(time));
                        }
                    }
                }
                metaMessage.add(ChatColor.DARK_GRAY + "-------------");
            }
            // Note: MapMeta is useless and not used
        }

        for (String s : metaMessage) {
            DumbAuction.getInstance().sendMessage(sender, s);
        }
    }

    private static String toTime(int duration) {
        double asMinutes = ((double) duration) / 60.0;
        int minutes = (int) Math.floor(asMinutes);
        int seconds = duration - (minutes * 60);
        return minutes + ":" + ((seconds + "").length() < 2 ? "0" + seconds : seconds);
    }

    private static String getChatName(ChatColor chat) {
        String name = "";
        String[] parts = chat.name().replace('_', ' ').split(" ");
        for (String s : parts)
            name += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
        return name.trim();
    }

    /**
     * Gets the player-friendly name for a firework type
     *
     * @param type the type, cannot be null
     * @return the player-friendly name
     */
    public static String getFireworkTypeName(FireworkEffect.Type type) {
        if (type == null) throw new IllegalArgumentException();

        switch (type) {
            case BALL:
                return "Ball";
            case BALL_LARGE:
                return "Large Ball";
            case STAR:
                return "Star";
            case CREEPER:
                return "Creeper";
            case BURST:
                return "Burst";
            default:
                return "Unknown";
        }
    }

    /**
     * Gets the player-friendly name for a potion effect type
     *
     * @param type the potion effect type, cannot be null
     * @return the player-friendly name
     */
    public static String getPotionEffectName(PotionEffectType type) {
        if (type == null) throw new IllegalArgumentException();

        if (type == PotionEffectType.DAMAGE_RESISTANCE)
            return "Damage Resistance";
        else if (type == PotionEffectType.BLINDNESS)
            return "Blindness";
        else if (type == PotionEffectType.FAST_DIGGING)
            return "Haste";
        else if (type == PotionEffectType.FIRE_RESISTANCE)
            return "Fire Resistance";
        else if (type == PotionEffectType.HARM)
            return "Harming";
        else if (type == PotionEffectType.HEAL)
            return "Healing";
        else if (type == PotionEffectType.HUNGER)
            return "Hunger";
        else if (type == PotionEffectType.JUMP)
            return "Jump Boost";
        else if (type == PotionEffectType.POISON)
            return "Poison";
        else if (type == PotionEffectType.REGENERATION)
            return "Regeneration";
        else if (type == PotionEffectType.SLOW)
            return "Slowness";
        else if (type == PotionEffectType.SPEED)
            return "Swiftness";
        else if (type == PotionEffectType.INCREASE_DAMAGE)
            return "Strength";
        else if (type == PotionEffectType.WATER_BREATHING)
            return "Water Breathing";
        else if (type == PotionEffectType.WEAKNESS)
            return "Weakness";
        else if (type == PotionEffectType.WITHER)
            return "Wither";
        else if (type == PotionEffectType.INVISIBILITY)
            return "Invisibility";
        else if (type == PotionEffectType.NIGHT_VISION)
            return "Night Vision";

        return "Unknown Potion Effect";
    }

    /**
     * Gets the name for an enchantment
     *
     * @param enchantment the enchantment to lookup, cannot be null
     * @return the player-friendly name for the enchantment
     */
    public static String getEnchantmentName(Enchantment enchantment) {
        if (enchantment == null) throw new IllegalArgumentException();

        if (enchantment == Enchantment.DAMAGE_ALL)
            return "Sharpness";
        else if (enchantment == Enchantment.DAMAGE_ARTHROPODS)
            return "Bane of Arthropods";
        else if (enchantment == Enchantment.DAMAGE_UNDEAD)
            return "Smite";
        else if (enchantment == Enchantment.DIG_SPEED)
            return "Efficiency";
        else if (enchantment == Enchantment.DURABILITY)
            return "Unbreaking";
        else if (enchantment == Enchantment.FIRE_ASPECT)
            return "Fire Aspect";
        else if (enchantment == Enchantment.KNOCKBACK)
            return "Knockback";
        else if (enchantment == Enchantment.LOOT_BONUS_BLOCKS)
            return "Fortune";
        else if (enchantment == Enchantment.LOOT_BONUS_MOBS)
            return "Looting";
        else if (enchantment == Enchantment.OXYGEN)
            return "Respiration";
        else if (enchantment == Enchantment.PROTECTION_ENVIRONMENTAL)
            return "Protection";
        else if (enchantment == Enchantment.PROTECTION_EXPLOSIONS)
            return "Blast Protection";
        else if (enchantment == Enchantment.PROTECTION_FALL)
            return "Feather Falling";
        else if (enchantment == Enchantment.PROTECTION_FIRE)
            return "Fire Protection";
        else if (enchantment == Enchantment.PROTECTION_PROJECTILE)
            return "Projectile Protection";
        else if (enchantment == Enchantment.SILK_TOUCH)
            return "Silk Touch";
        else if (enchantment == Enchantment.WATER_WORKER)
            return "Aqua Affinity";
        else if (enchantment == Enchantment.ARROW_FIRE)
            return "Fire Arrows";
        else if (enchantment == Enchantment.ARROW_DAMAGE)
            return "Power";
        else if (enchantment == Enchantment.ARROW_KNOCKBACK)
            return "Punch";
        else if (enchantment == Enchantment.ARROW_INFINITE)
            return "Infinity";

        return "Unknown Enchantment";
    }

    private static String integerToRomanNumeral(int input) {
        if (input < 1 || input > 3999)
            return "Invalid Roman Number Value";
        String s = "";
        while (input >= 1000) {
            s += "M";
            input -= 1000;
        }
        while (input >= 900) {
            s += "CM";
            input -= 900;
        }
        while (input >= 500) {
            s += "D";
            input -= 500;
        }
        while (input >= 400) {
            s += "CD";
            input -= 400;
        }
        while (input >= 100) {
            s += "C";
            input -= 100;
        }
        while (input >= 90) {
            s += "XC";
            input -= 90;
        }
        while (input >= 50) {
            s += "L";
            input -= 50;
        }
        while (input >= 40) {
            s += "XL";
            input -= 40;
        }
        while (input >= 10) {
            s += "X";
            input -= 10;
        }
        while (input >= 9) {
            s += "IX";
            input -= 9;
        }
        while (input >= 5) {
            s += "V";
            input -= 5;
        }
        while (input >= 4) {
            s += "IV";
            input -= 4;
        }
        while (input >= 1) {
            s += "I";
            input -= 1;
        }
        return s;
    }

    /**
     * Gets the name of the item passed
     *
     * @param item the item to check, cannot be null
     * @return the item name
     */
    public static String getName(ItemStack item) {
        return getName(item, false);
    }

    /**
     * Gets the name of the item passed
     *
     * @param item       the item to check, cannot be null
     * @param ignoreMeta if true, item meta will be ignored
     * @return the item name
     */
    public static String getName(ItemStack item, boolean ignoreMeta) {
        String def = item.getType().name();
        if (DumbAuction.getInstance().getWhatIsIt() != null) {
            def = DumbAuction.getInstance().getWhatIsIt().getName(item);
        } else {
            if (DumbAuction.getInstance().getConfig().getString("aliases." + item.getType().name()) != null) {
                def = DumbAuction.getInstance().getConfig().getString("aliases." + item.getType().name());
            }
        }

        DyeColor color = getDyeColor(item);
        if (color != null) {
            def = color.name() + "_" + def; // underscore is stripped later, capitals are also fixed
        }

        // ItemMeta always overrides everything else
        if (!ignoreMeta && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                def = ChatColor.ITALIC + meta.getDisplayName();
            }
        }

        String[] parts = def.split("_");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            builder.append(parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase());
            builder.append(" ");
        }
        return builder.toString().trim();
    }

    /**
     * Gets the dye color for a specified type of ItemStack.
     *
     * @param stack the stack to get the color of
     * @return the color of the stack, or null if not applicable (or invalid arguments)
     */
    public static DyeColor getDyeColor(ItemStack stack) {
        if (stack == null) return null;

        if (stack.getData() instanceof Colorable) {
            return ((Colorable) stack.getData()).getColor();
        }
        return null;
    }
}