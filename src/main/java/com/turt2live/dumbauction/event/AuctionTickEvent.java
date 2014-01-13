package com.turt2live.dumbauction.event;

import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.event.base.AuctionNotCancellableEvent;

/**
 * Fired when an auction timer is being ticked
 *
 * @author turt2live
 */
public class AuctionTickEvent extends AuctionNotCancellableEvent {

    protected long timeLeft;

    /**
     * Creates a new AuctionTickEvent
     *
     * @param auction  the applicable auction
     * @param timeLeft the number of seconds left in the auction
     */
    public AuctionTickEvent(Auction auction, long timeLeft) {
        super(auction);
        if (timeLeft < 0) throw new IllegalArgumentException();
        this.timeLeft = timeLeft;
    }

    /**
     * Gets the number of seconds left in an auction
     *
     * @return the number of seconds left in an auction
     */
    public long getTimeLeft() {
        return timeLeft;
    }

    /**
     * Sets the time left to 0, ending the auction.
     */
    public void endNow() {
        timeLeft = 0;
    }

    /**
     * Sets the number of seconds left
     *
     * @param timeLeft the number of seconds left, cannot be less than zero
     */
    public void setTimeLeft(long timeLeft) {
        if (timeLeft < 0) throw new IllegalArgumentException();
        this.timeLeft = timeLeft;
    }
}
