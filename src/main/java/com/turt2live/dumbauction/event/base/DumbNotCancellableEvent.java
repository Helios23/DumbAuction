package com.turt2live.dumbauction.event.base;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A dumb event that cannot be cancelled
 *
 * @author turt2live
 */
public class DumbNotCancellableEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
