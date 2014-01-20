package com.turt2live.dumbauction.rewards;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a store registry
 *
 * @author turt2live
 */
public class StoreRegistry {

    private List<RewardStore> stores = new ArrayList<RewardStore>();

    /**
     * Adds a reward store to the registry
     *
     * @param store the store to add
     */
    public void addStore(RewardStore store) {
        if (store == null) throw new IllegalArgumentException();
        this.stores.add(store);
    }

    /**
     * Removes a store from the registry
     *
     * @param store the store to remove
     */
    public void removeStore(RewardStore store) {
        if (store != null) {
            this.stores.remove(store);
        }
    }

    /**
     * Determines the most applicable store for the player. This returns the first found store
     *
     * @param player the player to look for, cannot be null
     * @return the applicable store, or null if none found
     */
    public RewardStore getApplicableStore(String player) {
        if (player == null) throw new IllegalArgumentException();
        for (RewardStore store : stores) {
            if (store.isApplicable(player)) {
                return store;
            }
        }
        return null;
    }

    /**
     * Saves all applicable stores
     */
    public void save() {
        for (RewardStore store : stores) {
            if (store instanceof SavingStore) {
                ((SavingStore) store).save();
            }
        }
    }

}
