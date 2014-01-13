package com.turt2live.dumbauction.event;

import com.turt2live.commonsense.event.DumbNotCancellableEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Fired when auction rewards cannot be given due to a full inventory
 *
 * @author turt2live
 */
public class RewardOverflowEvent extends DumbNotCancellableEvent {

    protected List<ItemStack> rewards = new ArrayList<ItemStack>();
    protected String rewardee;

    /**
     * Creates a new RewardOverflowEvent
     *
     * @param rewards  the applicable rewards, cannot be null
     * @param rewardee the player getting the rewards, cannot be null
     */
    public RewardOverflowEvent(List<ItemStack> rewards, String rewardee) {
        if (rewards == null || rewardee == null) throw new IllegalArgumentException();
        this.rewards.addAll(rewards);
        this.rewardee = rewardee;
    }

    /**
     * Gets a modifiable list of the rewards to be issued
     *
     * @return a modifiable list of rewards
     */
    public List<ItemStack> getRewards() {
        return rewards;
    }

    /**
     * Gets who is being rewarded the items
     *
     * @return who is being rewarded the items
     */
    public String getRewardee() {
        return rewardee;
    }

    /**
     * Sets who should receive the rewards
     *
     * @param rewardee the new rewardee, cannot be null
     */
    public void setRewardee(String rewardee) {
        if (rewardee == null) throw new IllegalArgumentException();
        this.rewardee = rewardee;
    }
}
