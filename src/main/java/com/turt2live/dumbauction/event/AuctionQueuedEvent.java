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
import com.turt2live.dumbauction.event.base.AuctionCancellableEvent;

/**
 * Fired when an auction has been queued
 *
 * @author turt2live
 */
public class AuctionQueuedEvent extends AuctionCancellableEvent {

    protected int currentPosition;

    /**
     * Creates a new AuctionQueuedEvent
     *
     * @param auction         the applicable auction
     * @param currentPosition the current position for the auction in the queue
     *
     * @see com.turt2live.dumbauction.auction.AuctionManager#getPosition(com.turt2live.dumbauction.auction.Auction)
     */
    public AuctionQueuedEvent(Auction auction, int currentPosition) {
        super(auction);
        if (currentPosition < 0) throw new IllegalArgumentException();
        this.currentPosition = currentPosition;
    }

    /**
     * Gets the current position in queue of the auction. If this event is cancelled, this number does not apply
     *
     * @return the current position in queue
     *
     * @see com.turt2live.dumbauction.auction.AuctionManager#getPosition(com.turt2live.dumbauction.auction.Auction)
     */
    public int getCurrentPosition() {
        return currentPosition;
    }
}
