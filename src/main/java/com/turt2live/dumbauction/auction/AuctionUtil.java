package com.turt2live.dumbauction.auction;

import com.turt2live.dumbauction.DumbAuction;
import com.turt2live.dumbauction.event.AuctionRewardEvent;
import com.turt2live.dumbauction.event.RewardOverflowEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Various utilities for managing auctions
 *
 * @author turt2live
 */
public class AuctionUtil {

    /**
     * Rewards items from an auction. If the auction has no bidders, the items are returned to
     * the seller.
     *
     * @param auction the auction to reward items from, cannot be null
     * @return true if the items were able to be rewarded
     */
    public static boolean rewardItems(Auction auction) {
        if (auction == null) throw new IllegalArgumentException();
        int amount = auction.getItemAmount();
        ItemStack stack = auction.getTemplateItem();
        String player = auction.getHighestBid() == null ? auction.getSeller() : auction.getHighestBid().getBidder();
        List<ItemStack> rewards = new ArrayList<ItemStack>();
        while (amount > 0) {
            ItemStack clone = stack.clone();
            int desiredAmount = clone.getType().getMaxStackSize();
            if (amount - desiredAmount < 0) desiredAmount = amount;
            clone.setAmount(desiredAmount);
            rewards.add(clone);
            amount -= desiredAmount;
        }

        AuctionRewardEvent event = new AuctionRewardEvent(auction, rewards, player);
        DumbAuction.getInstance().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        rewards = event.getRewards();
        player = event.getRewardee();

        Player onlinePlayer = DumbAuction.getInstance().getServer().getPlayerExact(player);
        if (onlinePlayer != null) {
            HashMap<Integer, ItemStack> overflow = onlinePlayer.getInventory().addItem(rewards.toArray(new ItemStack[0]));
            if (overflow != null && !overflow.isEmpty()) {
                List<ItemStack> over = new ArrayList<ItemStack>();
                over.addAll(overflow.values());
                DumbAuction.getInstance().getServer().getPluginManager().callEvent(new RewardOverflowEvent(over, onlinePlayer.getName()));
            }
        } else {
            for (ItemStack item : rewards) {
                DumbAuction.getInstance().getQueue().addToQueue(player, item);
            }
        }
        return true;
    }

}
