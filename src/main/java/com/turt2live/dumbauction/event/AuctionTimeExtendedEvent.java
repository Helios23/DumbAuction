package com.turt2live.dumbauction.event;

import com.turt2live.dumbauction.auction.Auction;

/**
 * Fired when an auction timer is extended
 *
 * @author turt2live
 */
public class AuctionTimeExtendedEvent extends AuctionTickEvent {

    protected long originalTimeLeft;

    /**
     * Creates a new AuctionTimeExtendedEvent
     *
     * @param auction          the applicable auction
     * @param timeLeft         the number of seconds left in the auction
     * @param originalTimeLeft the number of seconds this auction was expected to have left
     */
    public AuctionTimeExtendedEvent(Auction auction, long timeLeft, long originalTimeLeft) {
        super(auction, timeLeft);
        if (timeLeft < 0) throw new IllegalArgumentException();
        this.timeLeft = timeLeft;
    }

    /**
     * Gets how long the extension is. If this returns a negative number, the original time
     * left was modified by a previous event handler.
     *
     * @return the number of seconds the extension is
     */
    public long getExtension() {
        // 10 - 15 = -5; -(-5) = 5
        // 15 - 10 = 5; -(5) = -5
        return -(originalTimeLeft - timeLeft);
    }

    /**
     * Gets the number of seconds this auction was supposed to have left (before this event)
     *
     * @return the original time left
     */
    public long getOriginalTimeLeft() {
        return originalTimeLeft;
    }
}
