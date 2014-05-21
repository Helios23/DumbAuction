/*******************************************************************************
 * Copyright (C) 2014 Travis Ralston (turt2live)
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.turt2live.dumbauction.event;

import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.event.base.AuctionCancellableEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Fired when auction rewards are being distributed
 *
 * @author turt2live
 */
public class AuctionRewardEvent extends AuctionCancellableEvent {

    protected List<ItemStack> rewards = new ArrayList<ItemStack>();
    protected String rewardee;

    /**
     * Creates a new AuctionRewardEvent
     *
     * @param auction  the auction being rewarded, cannot be null
     * @param rewards  the applicable rewards, cannot be null
     * @param rewardee the player getting the rewards, cannot be null
     */
    public AuctionRewardEvent(Auction auction, List<ItemStack> rewards, String rewardee) {
        super(auction);
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
