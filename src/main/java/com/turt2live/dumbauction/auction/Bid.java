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
    private boolean isReserved;
    private DumbAuction plugin = DumbAuction.getInstance();

    /**
     * Creates a new bid
     *
     * @param bidder the bidder, cannot be null
     * @param amount the amount to bid, must be greater than zero
     */
    public Bid(String bidder, double amount) {
        if (bidder == null || amount <= 0)
            throw new IllegalArgumentException("bidder cannot be null. amount must be > 0");
        this.bidder = bidder;
        this.amount = amount;
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
        return DumbAuction.economy.has(bidder, amount);
    }

    /**
     * Reserves the funds required to complete this bid. If this fails, or has already been completed, this
     * returns false.
     *
     * @return true on success
     */
    public boolean reserveFunds() {
        if (!isReserved) {
            EconomyResponse response = DumbAuction.economy.withdrawPlayer(bidder, amount);
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
            EconomyResponse response = DumbAuction.economy.depositPlayer(bidder, amount);
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
