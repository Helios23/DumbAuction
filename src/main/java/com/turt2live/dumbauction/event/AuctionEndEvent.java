package com.turt2live.dumbauction.event;

import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.event.base.AuctionNotCancellableEvent;

/**
 * Fired when an auction has ended
 *
 * @author turt2live
 */
public class AuctionEndEvent extends AuctionNotCancellableEvent {

    /**
     * Creates a new AuctionEndEvent
     *
     * @param auction the applicable auction
     */
    public AuctionEndEvent(Auction auction) {
        super(auction);
    }
}
