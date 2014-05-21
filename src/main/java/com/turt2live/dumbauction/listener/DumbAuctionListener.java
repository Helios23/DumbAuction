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

package com.turt2live.dumbauction.listener;

import com.turt2live.dumbauction.DumbAuction;
import com.turt2live.dumbauction.auction.Auction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Listener for the Bukkit functions of DumbAuction
 *
 * @author turt2live
 */
public class DumbAuctionListener implements Listener {

    private static final String METADATA_MOVE = "dumbauction.move";

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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        if (!player.hasPermission("dumbauction.admin") && !plugin.getConfig().getBoolean("auctions.allow-world-change", true)) {
            Auction auction = plugin.getAuctionManager().getActiveAuction();
            if (auction == null) return;
            if (auction.getRealSeller().equalsIgnoreCase(player.getName())
                    || (auction.getHighestBid() != null && auction.getHighestBid().getRealBidder().equalsIgnoreCase(player.getName()))) {
                if (player.hasMetadata(METADATA_MOVE)) {
                    final Location location = (Location) player.getMetadata(METADATA_MOVE).get(0).value();
                    if (location.getWorld().equals(player.getWorld())) return; // Don't warn for a return trip.
                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                        public void run() {
                            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            plugin.sendMessage(player, ChatColor.RED + "You cannot change worlds right now.");
                        }
                    });
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.getConfig().getBoolean("auctions.allow-world-change", true)) return;
        if (isSignificantMove(event.getFrom(), event.getTo())) {
            Player player = event.getPlayer();
            if (!player.hasPermission("dumbauction.admin")) {
                player.setMetadata(METADATA_MOVE, new FixedMetadataValue(plugin, event.getTo()));
            }
        }
    }

    private boolean isSignificantMove(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
    }

}
