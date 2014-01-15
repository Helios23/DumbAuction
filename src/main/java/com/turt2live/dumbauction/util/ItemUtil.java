package com.turt2live.dumbauction.util;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
            if (stack == null && template == null) count++;
            else if (stack != null && template.isSimilar(stack)) count++;
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

}