package com.turt2live.dumbauction;

import com.turt2live.dumbauction.event.AuctionStartEvent;
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
        // TODO: Lang
    }

}
