package com.turt2live.dumbauction.event;

import com.turt2live.commonsense.event.DumbCancellableEvent;
import com.turt2live.dumbauction.auction.Auction;

/**
 * Represents an auction event
 *
 * @author turt2live
 */
public abstract class AuctionEvent extends DumbCancellableEvent {

    private Auction auction;

    protected AuctionEvent(Auction auction) {
        if (auction == null) throw new IllegalArgumentException();
        this.auction = auction;
    }

    /**
     * Gets the auction applicable to this event
     *
     * @return the applicable auction
     */
    public Auction getAuction() {
        return auction;
    }

}
