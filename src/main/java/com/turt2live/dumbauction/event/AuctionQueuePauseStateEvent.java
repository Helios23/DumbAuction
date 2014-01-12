package com.turt2live.dumbauction.event;

import com.turt2live.dumbauction.auction.AuctionManager;
import com.turt2live.dumbauction.event.base.DumbCancellableEvent;

/**
 * Called when the auction manager (queue) is paused or unpaused.
 *
 * @author turt2live
 */
public class AuctionQueuePauseStateEvent extends DumbCancellableEvent {

    private boolean isPausing;
    private AuctionManager manager;

    /**
     * Creates a new AuctionQueuePauseStateEvent
     *
     * @param auctionManager the auction manager being (un)paused
     * @param isPausing      the new state of the manager
     */
    public AuctionQueuePauseStateEvent(AuctionManager auctionManager, boolean isPausing) {
        if (auctionManager == null) throw new IllegalArgumentException();
        this.manager = auctionManager;
        this.isPausing = isPausing;
    }

    /**
     * Gets the AuctionManager applicable to this event
     *
     * @return the AuctionManager
     */
    public AuctionManager getManager() {
        return manager;
    }

    /**
     * Determines if the new state of the AuctionManager is to be paused or unpaused
     *
     * @return the new state
     */
    public boolean isPausing() {
        return isPausing;
    }

}
