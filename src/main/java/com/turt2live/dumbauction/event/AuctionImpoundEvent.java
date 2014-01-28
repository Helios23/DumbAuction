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
