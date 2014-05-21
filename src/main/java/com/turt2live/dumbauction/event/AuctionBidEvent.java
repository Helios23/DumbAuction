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
import com.turt2live.dumbauction.auction.Bid;
import com.turt2live.dumbauction.event.base.AuctionCancellableEvent;

/**
 * Fired when a valid bid is submitted to an auction
 *
 * @author turt2live
 */
public class AuctionBidEvent extends AuctionCancellableEvent {

    protected Bid bid;

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
