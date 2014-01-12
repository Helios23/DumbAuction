package com.turt2live.dumbauction.event;

import com.turt2live.dumbauction.auction.Auction;
import org.bukkit.command.CommandSender;

/**
 * Fired when an auction is cancelled
 *
 * @author turt2live
 */
public class AuctionCancelEvent extends AuctionEvent {

    /**
     * Possible causes for an auction to be cancelled
     */
    public enum CancelCause {
        /**
         * The manager is stopping and has issued a cancel of the auction.
         * <p/>
         * In this cause, the cancellation status of the event is ignored
         */
        MANAGER_STOP,

        /**
         * The auction was cancelled by command
         */
        COMMAND,

        /**
         * The auction was cancelled by unknown means (hooking plugin?)
         */
        MAGIC;
    }

    private CancelCause cause;
    private CommandSender canceller;

    /**
     * Creates a new AuctionCancelEvent
     *
     * @param auction the auction that is being cancelled
     * @param cause   the cause of the cancellation
     * @throws IllegalArgumentException if the cause if of type CancelCause.COMMAND, use {@link #AuctionCancelEvent(com.turt2live.dumbauction.auction.Auction, com.turt2live.dumbauction.event.AuctionCancelEvent.CancelCause, org.bukkit.command.CommandSender)} instead
     */
    public AuctionCancelEvent(Auction auction, CancelCause cause) {
        super(auction);
        if (cause == null || cause == CancelCause.COMMAND) throw new IllegalArgumentException();
        this.cause = cause;
    }

    /**
     * Creates a new AuctionCancelEvent
     *
     * @param auction   the auction that is being cancelled
     * @param cause     the cause of the cancellation
     * @param canceller the entity cancelling the auction
     * @throws IllegalArgumentException if the cause is not of type CancelCause.COMMAND, use {@link #AuctionCancelEvent(com.turt2live.dumbauction.auction.Auction, com.turt2live.dumbauction.event.AuctionCancelEvent.CancelCause)} instead
     */
    public AuctionCancelEvent(Auction auction, CancelCause cause, CommandSender canceller) {
        super(auction);
        if (cause == null || cause == CancelCause.COMMAND || canceller == null) throw new IllegalArgumentException();
        this.cause = cause;
        this.canceller = canceller;
    }

    /**
     * The cause of the cancel. If of type CancelCause.MANAGER_STOP, the cancelled state of this event
     * is ignored
     *
     * @return the cause of the auction cancel
     */
    public CancelCause getCause() {
        return cause;
    }

    /**
     * Gets the canceller of this auction. Only applicable if {@link #getCause()} is of type CancelCause.COMMAND
     *
     * @return the canceller, or null if not applicable
     */
    public CommandSender getCanceller() {
        return canceller;
    }
}
