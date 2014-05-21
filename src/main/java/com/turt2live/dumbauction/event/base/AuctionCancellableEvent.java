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

package com.turt2live.dumbauction.event.base;

import com.turt2live.commonsense.event.DumbCancellableEvent;
import com.turt2live.dumbauction.auction.Auction;

/**
 * Represents an auction event
 *
 * @author turt2live
 */
public abstract class AuctionCancellableEvent extends DumbCancellableEvent implements AuctionEvent {

    protected Auction auction;

    protected AuctionCancellableEvent(Auction auction) {
        if (auction == null) throw new IllegalArgumentException();
        this.auction = auction;
    }

    @Override
    public Auction getAuction() {
        return auction;
    }

}
