package com.turt2live.dumbauction;

import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.event.*;
import com.turt2live.dumbauction.rewards.RewardStore;
import com.turt2live.dumbauction.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Internal listener for plugin operation
 */
public class InternalListener implements Listener {

    private DumbAuction plugin = DumbAuction.getInstance();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionStart(AuctionStartEvent event) {
        String seller = plugin.getConfig().getBoolean("auctions.use-displayname", true) ? event.getAuction().getSeller() : event.getAuction().getRealSeller();
        String startCost = plugin.getEconomy().format(event.getAuction().getMinimumBid());
        String bidIncrement = plugin.getEconomy().format(event.getAuction().getBidIncrement());
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
        if (event.getExtension() > 0)
            plugin.broadcast(ChatColor.GRAY + "The auction time has been extended by " + ChatColor.AQUA + "" + event.getExtension() + " seconds");
        else if (event.getExtension() < 0)
            plugin.broadcast(ChatColor.GRAY + "The auction time has been shortened by " + ChatColor.AQUA + "" + (event.getExtension() * -1) + " seconds");
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
        if (event.getClass().equals(AuctionBidEvent.class)) {
            plugin.broadcast(ChatColor.AQUA + event.getBid().getBidder() + ChatColor.GRAY + " has bid " + ChatColor.AQUA + plugin.getEconomy().format(event.getBid().getAmount()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAuctionReward(AuctionRewardEvent event) {
        Auction auction = event.getAuction();
        String rewardee = event.getRewardee();
        if (!auction.getRealSeller().equalsIgnoreCase(rewardee)) {
            plugin.broadcast(ChatColor.AQUA + rewardee + ChatColor.GRAY + " has won the auction with " + ChatColor.AQUA + plugin.getEconomy().format(auction.getHighestBid().getAmount()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOverflow(RewardOverflowEvent event) {
        Player player = plugin.getServer().getPlayerExact(event.getRewardee());
        for (ItemStack item : event.getRewards()) {
            if (player != null) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            } else {
                RewardStore store = plugin.getRewardStores().getApplicableStore(event.getRewardee());
                if (store != null) {
                    store.store(event.getRewardee(), item);
                }
            }
        }
        if (player != null)
            plugin.sendMessage(player, ChatColor.RED + "Your inventory was full and some items were dropped.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onImpound(AuctionImpoundEvent event) {
        plugin.broadcast(ChatColor.AQUA + event.getImpounder().getName() + ChatColor.RED + " has impounded the auction");
    }
}
