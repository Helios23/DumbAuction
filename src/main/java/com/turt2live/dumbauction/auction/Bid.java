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

import com.turt2live.dumbauction.DumbAuction;
import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Represents a bid on an auction
 *
 * @author turt2live
 */
public class Bid {

    private double amount;
    private String bidder;
    private String realBidder;
    private boolean isReserved;
    private DumbAuction plugin = DumbAuction.getInstance();

    /**
     * Creates a new bid
     *
     * @param bidder     the bidder, cannot be null
     * @param realBidder the real bidder's name, cannot be null
     * @param amount     the amount to bid, must be greater than zero
     */
    public Bid(String bidder, String realBidder, double amount) {
        if (bidder == null || amount <= 0 || realBidder == null)
            throw new IllegalArgumentException("bidder cannot be null. amount must be > 0");
        this.bidder = bidder;
        this.realBidder = realBidder;
        this.amount = amount;
    }

    /**
     * Gets the real bidder's name
     *
     * @return the real bidder's name
     */
    public String getRealBidder() {
        return realBidder;
    }

    /**
     * Gets who is bidding
     *
     * @return the bidder
     */
    public String getBidder() {
        return bidder;
    }

    /**
     * Gets the bid amount
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Determines if the bidder for this object has enough funds
     *
     * @return true if the bidder has enough funds
     */
    public boolean hasEnough() {
        return plugin.getEconomy().has(realBidder, amount);
    }

    /**
     * Reserves the funds required to complete this bid. If this fails, or has already been completed, this
     * returns false.
     *
     * @return true on success
     */
    public boolean reserveFunds() {
        if (!isReserved) {
            EconomyResponse response = plugin.getEconomy().withdrawPlayer(realBidder, amount);
            if (response.transactionSuccess()) {
                isReserved = true;
            } else {
                isReserved = false;
            }
            return isReserved;
        }
        return false; // Already reserved
    }

    /**
     * Determines if the funds for this bid have been reserved
     *
     * @return true if reserved
     */
    public boolean isReserved() {
        return isReserved;
    }

    /**
     * Returns the funds required for this bid to the bidder. If this has already been done, or the funds are not
     * reserved, this returns false.
     *
     * @return true on success
     */
    public boolean returnFunds() {
        if (isReserved) {
            EconomyResponse response = plugin.getEconomy().depositPlayer(realBidder, amount);
            if (response.transactionSuccess()) {
                isReserved = false;
            } else {
                isReserved = true;
            }
            return !isReserved;
        }
        return false; // No funds to return
    }

}
