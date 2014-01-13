package com.turt2live.dumbauction.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Item utility methods
 * @author turt2live
 */
public class ItemUtil {

    /**
     * Counts how many of an item stack an inventory contains
     * @param template the template to use, amount is ignored
     *                 @param inventory the inventory to check
     * @return the total number of items this inventory contains
     */
    public static int getCount(ItemStack template, Inventory inventory){
        if(inventory==null)throw new IllegalArgumentException();
        int count = 0;
        for(ItemStack stack : inventory.getContents()){
            if(stack==null&&template==null)count++;
            else if(stack!=null&&template.isSimilar(stack))count++;
        }
        return count;
    }

}