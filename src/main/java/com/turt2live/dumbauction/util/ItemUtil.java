package com.turt2live.dumbauction.util;

import com.turt2live.dumbauction.DumbAuction;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
     * Sends a display of item information to the specified command sender
     *
     * @param item   the item to display, cannot be null
     * @param sender the command sender to send the display to, cannot be null
     */
    public static void showInformation(ItemStack item, CommandSender sender) {
        if (item == null || sender == null) throw new IllegalArgumentException();
        // TODO
        sender.sendMessage("NYI");
    }

    /**
     * Sends a display of item information to the specified command sender
     *
     * @param item      the item to display, cannot be null
     * @param sender    the command sender to send the display to, cannot be null
     * @param placement the number to prefix the message. If less than zero, nothing is appended
     */
    public static void showQuickInformation(ItemStack item, CommandSender sender, int placement) {
        if (item == null || sender == null) throw new IllegalArgumentException();
        // TODO
        sender.sendMessage((placement >= 0 ? "#" + placement : "") + "NYI");
    }

    /**
     * Gets the name of the item passed
     *
     * @param item the item to check, cannot be null
     * @return the item name
     */
    public static String getName(ItemStack item) {
        String def = item.getType().name();
        if (DumbAuction.getInstance().getWhatIsIt() != null) {
            def = DumbAuction.getInstance().getWhatIsIt().getName(item);
        } else {
            if (DumbAuction.getInstance().getConfig().getString("aliases." + item.getType().name()) != null) {
                def = DumbAuction.getInstance().getConfig().getString("aliases." + item.getType().name());
            }
        }

        // ItemMeta always overrides everything else
        if (item.hasItemMeta()) {
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
}