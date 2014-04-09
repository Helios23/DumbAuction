package com.turt2live.dumbauction;

import com.turt2live.dumbauction.auction.Auction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Listener for the Bukkit functions of DumbAuction
 *
 * @author turt2live
 */
public class DumbAuctionListener implements Listener {

    private DumbAuction plugin = DumbAuction.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("dumbauction.admin") && !plugin.getConfig().getBoolean("auctions.allow-gamemode-change", false)) {
            Auction auction = plugin.getAuctionManager().getActiveAuction();
            if (auction == null) return;
            if (auction.getRealSeller().equalsIgnoreCase(player.getName())
                    || (auction.getHighestBid() != null && auction.getHighestBid().getRealBidder().equalsIgnoreCase(player.getName()))) {
                event.setCancelled(true);
                plugin.sendMessage(player, ChatColor.RED + "You cannot change game modes right now.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWorldChange(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getWorld().equals(event.getFrom().getWorld())) return;
        if (!player.hasPermission("dumbauction.admin") && !plugin.getConfig().getBoolean("auctions.allow-world-change", true)) {
            Auction auction = plugin.getAuctionManager().getActiveAuction();
            if (auction == null) return;
            if (auction.getRealSeller().equalsIgnoreCase(player.getName())
                    || (auction.getHighestBid() != null && auction.getHighestBid().getRealBidder().equalsIgnoreCase(player.getName()))) {
                event.setCancelled(true);
                plugin.sendMessage(player, ChatColor.RED + "You cannot change worlds right now.");
            }
        }
    }

}
