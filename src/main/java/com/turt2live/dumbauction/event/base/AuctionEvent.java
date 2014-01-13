package com.turt2live.dumbauction.event.base;

import com.turt2live.dumbauction.auction.Auction;

/**
 * Represents a basic AuctionEvent
 *
 * @author turt2live
 */
public interface AuctionEvent {

    /**
     * Gets the auction applicable to this event
     *
     * @return the applicable auction
     */
    public Auction getAuction();

}
