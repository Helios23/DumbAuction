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
import org.bukkit.entity.Player;

/**
 * Fired when an auction has been impounded
 *
 * @author turt2live
 */
public class AuctionImpoundEvent extends AuctionNotCancellableEvent {

    private Player impounder;

    /**
     * Creates a new AuctionImpoundEvent
     *
     * @param auction   the applicable auction
     * @param impounder the person who impounded the auction
     */
    public AuctionImpoundEvent(Auction auction, Player impounder) {
        super(auction);
        if (impounder == null) throw new IllegalArgumentException();
        this.impounder = impounder;
    }

    /**
     * Gets the person who impounded the auction
     *
     * @return the person who impounded the auction
     */
    public Player getImpounder() {
        return impounder;
    }
}
