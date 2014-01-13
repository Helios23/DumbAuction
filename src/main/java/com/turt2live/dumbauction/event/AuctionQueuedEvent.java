package com.turt2live.dumbauction.event;

import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.event.base.AuctionCancellableEvent;

/**
 * Fired when an auction has been queued
 *
 * @author turt2live
 */
public class AuctionQueuedEvent extends AuctionCancellableEvent {

    protected int currentPosition;

    /**
     * Creates a new AuctionQueuedEvent
     *
     * @param auction         the applicable auction
     * @param currentPosition the current position for the auction in the queue
     * @see com.turt2live.dumbauction.auction.AuctionManager#getPosition(com.turt2live.dumbauction.auction.Auction)
     */
    public AuctionQueuedEvent(Auction auction, int currentPosition) {
        super(auction);
        if (currentPosition < 0) throw new IllegalArgumentException();
        this.currentPosition = currentPosition;
    }

    /**
     * Gets the current position in queue of the auction. If this event is cancelled, this number does not apply
     *
     * @return the current position in queue
     * @see com.turt2live.dumbauction.auction.AuctionManager#getPosition(com.turt2live.dumbauction.auction.Auction)
     */
    public int getCurrentPosition() {
        return currentPosition;
    }
}
