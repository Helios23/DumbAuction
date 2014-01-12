package com.turt2live.dumbauction.event.base;

import org.bukkit.event.Cancellable;

/**
 * A dumb event that can be cancelled
 *
 * @author turt2live
 */
public abstract class DumbCancellableEvent extends DumbNotCancellableEvent implements Cancellable {

    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
