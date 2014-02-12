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
