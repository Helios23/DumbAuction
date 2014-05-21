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

import com.turt2live.commonsense.event.DumbCancellableEvent;
import com.turt2live.dumbauction.auction.AuctionManager;

/**
 * Called when the auction manager (queue) is paused or unpaused.
 *
 * @author turt2live
 */
public class AuctionQueuePauseStateEvent extends DumbCancellableEvent {

    protected boolean isPausing;
    protected AuctionManager manager;

    /**
     * Creates a new AuctionQueuePauseStateEvent
     *
     * @param auctionManager the auction manager being (un)paused
     * @param isPausing      the new state of the manager
     */
    public AuctionQueuePauseStateEvent(AuctionManager auctionManager, boolean isPausing) {
        if (auctionManager == null) throw new IllegalArgumentException();
        this.manager = auctionManager;
        this.isPausing = isPausing;
    }

    /**
     * Gets the AuctionManager applicable to this event
     *
     * @return the AuctionManager
     */
    public AuctionManager getManager() {
        return manager;
    }

    /**
     * Determines if the new state of the AuctionManager is to be paused or unpaused
     *
     * @return the new state
     */
    public boolean isPausing() {
        return isPausing;
    }

}
