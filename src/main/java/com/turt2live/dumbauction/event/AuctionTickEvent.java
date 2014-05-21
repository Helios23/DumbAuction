/*******************************************************************************
 * Copyright (C) 2014 Travis Ralston (turt2live)
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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
