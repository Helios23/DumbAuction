package com.turt2live.dumbauction;

import com.turt2live.dumbauction.auction.Auction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

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
            if (auction.getRealSeller().equalsIgnoreCase(player.getName())
                    || (auction.getHighestBid() != null && auction.getHighestBid().getBidder().equalsIgnoreCase(player.getName()))) {
                event.setCancelled(true);
                plugin.sendMessage(player, ChatColor.RED + "You cannot change game modes right now.");
            }
        }
    }

}
