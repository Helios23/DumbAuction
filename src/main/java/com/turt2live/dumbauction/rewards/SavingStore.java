package com.turt2live.dumbauction.rewards;

/**
 * A store that can be saved
 *
 * @author turt2live
 */
public interface SavingStore extends RewardStore {

    /**
     * Saves the store
     */
    public void save();

}
