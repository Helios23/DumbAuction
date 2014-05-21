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
import com.turt2live.dumbauction.event.base.AuctionCancellableEvent;
import org.bukkit.command.CommandSender;

/**
 * Fired when an auction is cancelled
 *
 * @author turt2live
 */
public class AuctionCancelEvent extends AuctionCancellableEvent {

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
        MAGIC,

        /**
         * The auction was cancelled due to an impound. The cancelled state of the {@link com.turt2live.dumbauction.event.AuctionCancelEvent} is ignored
         */
        IMPOUND;
    }

    protected CancelCause cause;
    protected CommandSender canceller;

    /**
     * Creates a new AuctionCancelEvent
     *
     * @param auction the auction that is being cancelled
     * @param cause   the cause of the cancellation
     *
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
     *
     * @throws IllegalArgumentException if the cause is not of type CancelCause.COMMAND, use {@link #AuctionCancelEvent(com.turt2live.dumbauction.auction.Auction, com.turt2live.dumbauction.event.AuctionCancelEvent.CancelCause)} instead
     */
    public AuctionCancelEvent(Auction auction, CancelCause cause, CommandSender canceller) {
        super(auction);
        if (cause == null || cause != CancelCause.COMMAND || canceller == null) throw new IllegalArgumentException();
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
