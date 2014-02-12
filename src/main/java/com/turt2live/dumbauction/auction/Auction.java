package com.turt2live.dumbauction.auction;

import com.turt2live.dumbauction.DumbAuction;
import com.turt2live.dumbauction.event.AuctionBidEvent;
import com.turt2live.dumbauction.event.AuctionSnipeEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an auction
 *
 * @author turt2live
 */
public class Auction {

    private ItemStack templateItem;
    private int amount;
    private double minimumBid;
    private double bidIncrement;
    private List<Bid> bids = new ArrayList<Bid>();
    private long time;
    private String seller;
    private String realSeller;
    private boolean wasCancelled = false;

    /**
     * Creates a new auction
     *
     * @param seller        the seller
     * @param realSeller    the real seller's name
     * @param startingPrice the starting price of the auction
     * @param bidIncrement  the bid increment for the auction
     * @param time          the time required for the auction
     * @param amount        the amount of the item to auction
     * @param item          the template item to auction
     */
    public Auction(String seller, String realSeller, double startingPrice, double bidIncrement, long time, int amount, ItemStack item) {
        if (seller == null || realSeller == null || startingPrice <= 0 || bidIncrement <= 0 || time <= 0 || amount <= 0 || item == null)
            throw new IllegalArgumentException();
        this.seller = seller;
        this.realSeller = realSeller;
        this.minimumBid = startingPrice;
        this.bidIncrement = bidIncrement;
        this.time = time;
        this.amount = amount;
        this.templateItem = item.clone();

        // Just in case...
        this.templateItem.setAmount(1);
    }

    /**
     * Gets the real name for the seller of this auction
     *
     * @return the auction seller's real name
     */
    public String getRealSeller() {
        return realSeller;
    }

    /**
     * Gets the seller of the auction
     *
     * @return the auction seller
     */
    public String getSeller() {
        return seller;
    }

    /**
     * Gets the minimum bid (starting price) for the auction
     *
     * @return the minimum bid
     */
    public double getMinimumBid() {
        return minimumBid;
    }

    /**
     * Gets the bid increment for the auction
     *
     * @return the bid increment
     */
    public double getBidIncrement() {
        return bidIncrement;
    }

    /**
     * Gets the total required time needed to run this auction. This is not updated as the auction
     * counts down.
     *
     * @return the total required time
     */
    public long getRequiredTime() {
        return time;
    }

    /**
     * Gets the amount of the item this auction is providing
     *
     * @return the amount of the item
     */
    public int getItemAmount() {
        return amount;
    }

    /**
     * Gets a template item of the item to be auctioned. This is not a live copy.
     *
     * @return the template item
     */
    public ItemStack getTemplateItem() {
        return templateItem.clone();
    }

    /**
     * Gets an UNMODIFIABLE list of all bids
     *
     * @return a list of all bids
     */
    public List<Bid> getAllBids() {
        return Collections.unmodifiableList(bids);
    }

    /**
     * Gets the highest bid or null if there is none
     *
     * @return the highest bid, or null if none
     */
    public Bid getHighestBid() {
        Bid maximum = null;
        for (Bid bid : bids) {
            if (maximum == null || bid.getAmount() > maximum.getAmount()) {
                maximum = bid;
            }
        }
        return maximum;
    }

    /**
     * Gets the next amount the next bid will have to be in order to be able to be accepted
     *
     * @return the next bid amount
     */
    public double getNextMinimum() {
        Bid highest = getHighestBid();
        if (highest != null) return highest.getAmount() + getBidIncrement();
        return getMinimumBid();
    }

    /**
     * Determines if a bid can be accepted by this auction. This will not actually apply the bid.
     *
     * @param bid the bid to test, cannot be null
     * @return true if the bid would be accepted
     */
    public boolean canAccept(Bid bid) {
        return bid != null && bid.getAmount() >= getNextMinimum();
    }

    /**
     * Submits a bid to the auction
     *
     * @param bid the bid to submit, cannot be null
     * @return true on success. false if the bid was rejected
     */
    public boolean submitBid(Bid bid) {
        if (bid != null) {
            if (canAccept(bid)) {
                AuctionBidEvent event = new AuctionBidEvent(this, bid);
                DumbAuction.getInstance().getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    Bid prevHigh = getHighestBid();
                    if (!bid.reserveFunds()) return false;
                    bids.add(bid);
                    if (prevHigh != null) prevHigh.returnFunds();

                    // Snipe detection
                    DumbAuction plugin = DumbAuction.getInstance();
                    int minTime = plugin.getConfig().getInt("snipe.time-left", 5);
                    long extension = plugin.getConfig().getLong("snipe.extend-seconds", 5);
                    if (minTime < 0) {
                        plugin.getLogger().warning("snipe.time-left of " + minTime + " is invalid. Must be greater than zero or zero to disable. Using 5 instead");
                        minTime = 5;
                    }
                    if (extension < 0) {
                        plugin.getLogger().warning("snipe.extend-seconds of " + extension + " is invalid. Must be greater than zero or zero to disable. Using 5 instead");
                        extension = 5;
                    }
                    if (minTime > 0 && extension > 0 && plugin.getAuctionManager().getAuctionTimeLeft() <= minTime) {
                        AuctionSnipeEvent snipeEvent = new AuctionSnipeEvent(this, bid, extension);
                        plugin.getServer().getPluginManager().callEvent(snipeEvent);
                        if (!snipeEvent.isCancelled()) {
                            extension = snipeEvent.getExtension();
                            if (extension > 0) {
                                plugin.getAuctionManager().setAuctionTimeLeft(plugin.getAuctionManager().getAuctionTimeLeft() + extension);
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if this auction was cancelled
     *
     * @return true if cancelled, false otherwise
     */
    public boolean wasCancelled() {
        return wasCancelled;
    }

    private void refundCancel() {
        if (getHighestBid() != null) getHighestBid().returnFunds();
        this.wasCancelled = true;
    }

    void cancel() {
        refundCancel();
        AuctionUtil.rewardItems(this); // Return items
    }

    void impound(Player player) {
        refundCancel();
        AuctionUtil.impoundItems(this, player);
    }

}
