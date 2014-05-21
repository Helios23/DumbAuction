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

package com.turt2live.dumbauction.auction;

/**
 * A bid used for buying items
 *
 * @author turt2live
 */
public class BuyingBid extends Bid {

    /**
     * Creates a new bid
     *
     * @param bidder     the bidder, cannot be null
     * @param realBidder the real bidder's name, cannot be null
     * @param amount     the amount to bid, must be greater than zero
     */
    public BuyingBid(String bidder, String realBidder, double amount) {
        super(bidder, realBidder, amount);
    }

}
