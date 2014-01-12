package com.turt2live.dumbauction.event;

import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.auction.Bid;

/**
 * Called when an auction is sniped with a bid. This means the auction was nearing completion when someone
 * bid on it, extending the time.
 *
 * @author turt2live
 */
public class AuctionSnipeEvent extends AuctionBidEvent {

    /**
     * Creates a new AuctionSnipeEvent
     *
     * @param auction the applicable auction
     * @param bid     the applicable bid
     */
    public AuctionSnipeEvent(Auction auction, Bid bid) {
        super(auction, bid);
    }
}
