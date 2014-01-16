package com.turt2live.dumbauction;

import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.event.*;
import com.turt2live.dumbauction.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Internal listener for plugin operation
 */
public class InternalListener implements Listener {

    private DumbAuction plugin = DumbAuction.getInstance();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionStart(AuctionStartEvent event) {
        String seller = event.getAuction().getSeller();
        String startCost = DumbAuction.economy.format(event.getAuction().getMinimumBid());
        String bidIncrement = DumbAuction.economy.format(event.getAuction().getBidIncrement());
        String time = event.getAuction().getRequiredTime() + " seconds";
        String itemName = ItemUtil.getName(event.getAuction().getTemplateItem());

        // Send messages
        plugin.broadcast(ChatColor.GOLD + seller + ChatColor.YELLOW + " has started an auction for " + ChatColor.GOLD + event.getAuction().getItemAmount() + "x " + itemName + ChatColor.YELLOW + " for " + ChatColor.GOLD + time);
        plugin.broadcast(ChatColor.GRAY + "Starting Price: " + ChatColor.AQUA + startCost + "   " + ChatColor.GRAY + "Bid Increment: " + ChatColor.AQUA + bidIncrement);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionTick(AuctionTickEvent event) {
        if (event.getTimeLeft() <= 0) return;
        if (event.getTimeLeft() <= 3 || event.getTimeLeft() % 15 == 0) {
            plugin.broadcast(ChatColor.AQUA + "" + event.getTimeLeft() + " " + ChatColor.GRAY + "seconds left!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionExtend(AuctionTimeExtendedEvent event) {
        plugin.broadcast(ChatColor.GRAY + "The auction time has been extended by " + ChatColor.AQUA + "" + event.getExtension() + " seconds");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionSnipe(AuctionSnipeEvent event) {
        plugin.broadcast(ChatColor.YELLOW + "SNIPED! Auction time extended.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionEnd(AuctionEndEvent event) {
        plugin.broadcast(ChatColor.GRAY + "The auction has ended");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionCancel(AuctionCancelEvent event) {
        plugin.broadcast(ChatColor.GRAY + "The auction has been cancelled");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionPauseChange(AuctionQueuePauseStateEvent event) {
        plugin.broadcast(ChatColor.YELLOW + "The queue has been " + (event.isPausing() ? ChatColor.RED + "PAUSED" : ChatColor.GREEN + "RESUMED"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionBid(AuctionBidEvent event) {
        plugin.broadcast(ChatColor.AQUA + event.getBid().getBidder() + ChatColor.GRAY + " has bid " + ChatColor.AQUA + DumbAuction.economy.format(event.getBid().getAmount()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionReward(AuctionRewardEvent event) {
        Auction auction = event.getAuction();
        String rewardee = event.getRewardee();
        if (!auction.getSeller().equalsIgnoreCase(rewardee)) {
            plugin.broadcast(ChatColor.AQUA + rewardee + ChatColor.GRAY + " has won the auction with " + ChatColor.AQUA + DumbAuction.economy.format(auction.getHighestBid().getAmount()));
        }
    }

}
