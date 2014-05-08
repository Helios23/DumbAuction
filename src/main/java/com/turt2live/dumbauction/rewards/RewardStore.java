package com.turt2live.dumbauction.rewards;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Represents a reward store. This is used to store rewards for a later time
 *
 * @author turt2live
 */
public interface RewardStore {

    /**
     * Stores a player's rewards
     *
     * @param player  the player to store, cannot be null
     * @param rewards the rewards to store, cannot be null
     */
    public void store(UUID player, List<ItemStack> rewards);

    /**
     * Stores a single item in the store.
     *
     * @param player the player to store, cannot be null
     * @param item   the reward to store, cannot be null
     */
    public void store(UUID player, ItemStack item);

    /**
     * Distributes a player's rewards to them
     *
     * @param player       the player's store, cannot be null
     * @param distributeTo the player to distribute the store to. If null, the 'player' is assumed
     * @return true if items were distributed, false otherwise
     */
    public boolean distributeStore(UUID player, Player distributeTo);

    /**
     * Gets the store for a player
     *
     * @param player the player name
     * @return the store for the player
     */
    public List<ItemStack> getStore(UUID player);

    /**
     * Clears a store for a player
     *
     * @param player the player store to clear, cannot be null
     */
    public void clearStore(UUID player);

    /**
     * Determines if this store is applicable for the player
     *
     * @param player the player to test, cannot be null
     * @return true if this store is applicable to the specified player
     */
    public boolean isApplicable(UUID player);

}
