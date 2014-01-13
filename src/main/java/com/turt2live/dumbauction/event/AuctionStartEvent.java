package com.turt2live.dumbauction.event;

import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.event.base.AuctionNotCancellableEvent;

/**
 * Fired when an auction has started
 *
 * @author turt2live
 */
public class AuctionStartEvent extends AuctionNotCancellableEvent {

    /**
     * Creates a new AuctionStartEvent
     *
     * @param auction the applicable auction
     */
    public AuctionStartEvent(Auction auction) {
        super(auction);
    }
}
