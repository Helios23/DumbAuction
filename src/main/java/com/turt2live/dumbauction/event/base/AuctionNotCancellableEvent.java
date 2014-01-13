package com.turt2live.dumbauction.event.base;

import com.turt2live.commonsense.event.DumbNotCancellableEvent;
import com.turt2live.dumbauction.auction.Auction;

/**
 * Represents an auction event
 *
 * @author turt2live
 */
public abstract class AuctionNotCancellableEvent extends DumbNotCancellableEvent implements AuctionEvent {

    protected Auction auction;

    protected AuctionNotCancellableEvent(Auction auction) {
        if (auction == null) throw new IllegalArgumentException();
        this.auction = auction;
    }

    @Override
    public Auction getAuction() {
        return auction;
    }

}
