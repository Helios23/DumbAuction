package com.turt2live.dumbauction.event;

import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.auction.Bid;
import com.turt2live.dumbauction.event.base.AuctionEvent;

/**
 * Fired when a valid bid is submitted to an auction
 *
 * @author turt2live
 */
public class AuctionBidEvent extends AuctionEvent {

    private Bid bid;

    /**
     * Creates a new AuctionBidEvent
     *
     * @param auction the applicable auction
     * @param bid     the applicable bid
     */
    public AuctionBidEvent(Auction auction, Bid bid) {
        super(auction);
        if (bid == null) throw new IllegalArgumentException();
        this.bid = bid;
    }

    /**
     * Gets the applicable bid
     *
     * @return the applicable bid
     */
    public Bid getBid() {
        return bid;
    }
}
