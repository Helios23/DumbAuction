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

    protected long extension;

    /**
     * Creates a new AuctionSnipeEvent
     *
     * @param auction   the applicable auction
     * @param bid       the applicable bid
     * @param extension the number of seconds this auction will be extended by
     */
    public AuctionSnipeEvent(Auction auction, Bid bid, long extension) {
        super(auction, bid);
        if (extension < 0) throw new IllegalArgumentException();
        this.extension = extension;
    }

    /**
     * Gets the amount of time this auction will be extended by
     *
     * @return the number of seconds this auction will be extended by
     */
    public long getExtension() {
        return extension;
    }

    /**
     * Sets the number of seconds to extend this auction by. Zero simply does nothing.
     *
     * @param extension the number of seconds to extend the auction by, cannot be less than zero
     */
    public void setExtension(long extension) {
        if (extension < 0) throw new IllegalArgumentException();
        this.extension = extension;
    }
}
