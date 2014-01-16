package com.turt2live.dumbauction.command.validator;

import com.turt2live.dumbauction.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Used for validating a player has a specific amount of items in their inventory
 *
 * @author turt2live
 */
public class InventoryAmountValidator implements ArgumentValidator {

    private static final String NO_HAND = "You are not holding an item!";
    private static final String NOT_A_PLAYER = "You are not a player!";
    private static final String NOT_A_NUMBER = "Please supply a valid whole number";
    private static final String NOT_ENOUGH = "You do not have enough of that item!";

    // Stored for a short time
    private Map<String, String> errors = new HashMap<String, String>();

    @Override
    public boolean isValid(CommandSender sender, String input) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack hand = player.getItemInHand();
            if (hand == null || hand.getType() == Material.AIR) {
                errors.put(sender.getName(), NO_HAND);
                return false;
            } else {
                int maximum = ItemUtil.getCount(hand, player.getInventory());
                try {
                    int amount = (input.equalsIgnoreCase("*") || input.equalsIgnoreCase("all")) ? maximum : Integer.parseInt(input);
                    if (amount > maximum) {
                        errors.put(sender.getName(), NOT_ENOUGH);
                        return false;
                    } else {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    errors.put(sender.getName(), NOT_A_NUMBER);
                    return false;
                }
            }
        }
        errors.put(sender.getName(), NOT_A_PLAYER);
        return false;
    }

    @Override
    public Integer get(CommandSender sender, String input) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ItemStack hand = player.getItemInHand();
                if (hand == null || hand.getType() == Material.AIR) return 0;
                int maximum = ItemUtil.getCount(hand, player.getInventory());
                return (input.equalsIgnoreCase("*") || input.equalsIgnoreCase("all")) ? maximum : Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    @Override
    public String getErrorMessage(CommandSender sender, String input) {
        return ChatColor.RED + errors.get(sender.getName());
    }

    @Override
    public void setArguments(String[] arguments) {
    }
}
