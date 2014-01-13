package com.turt2live.dumbauction.auction;

import com.turt2live.dumbauction.DumbAuction;
import com.turt2live.dumbauction.event.*;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Represents the auction manager. This handles all the ticking of auctions and the flow
 * of said auctions.
 *
 * @author turt2live
 */
public class AuctionManager {

    private DumbAuction plugin = DumbAuction.getInstance();
    private int maxQueueSize = plugin.getConfig().getInt("max-queue-size", 3);
    private long downtimeTicks = plugin.getConfig().getLong("seconds-between-auctions", 15);

    private ArrayBlockingQueue<Auction> auctions;
    private long currentDowntime = 0, auctionTimeLeft = 0;

    private Auction activeAuction;
    private boolean paused = false;
    private AuctionTask task;

    public AuctionManager() {
        // Prepare variables
        maxQueueSize = plugin.getConfig().getInt("max-queue-size", 3);
        downtimeTicks = plugin.getConfig().getLong("seconds-between-auctions", 15);

        // Validation
        if (maxQueueSize < 0) {
            plugin.getLogger().warning("max-queue-size of " + maxQueueSize + " is invalid. Must be greater than or equal to 1. Using 3 instead");
            maxQueueSize = 3;
        }
        if (downtimeTicks < 0) {
            plugin.getLogger().warning("seconds-between-auctions of " + downtimeTicks + " is invalid. Must be greater than or equal to zero. Using 15 instead");
            downtimeTicks = 15;
        }

        // Prepare queue
        auctions = new ArrayBlockingQueue<Auction>(maxQueueSize);

        // Prepare task
        task = new AuctionTask(this);
        task.runTaskTimer(plugin, 20L, 20L); // once a second
    }

    /**
     * Sets whether or not the auction manager is paused
     *
     * @param paused paused status
     * @return true if the paused state was set. Returns true if the passed state is the current state
     */
    public boolean setPaused(boolean paused) {
        boolean before = this.paused;
        if (before != paused) {
            AuctionQueuePauseStateEvent event = new AuctionQueuePauseStateEvent(this, paused);
            plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;
            this.paused = paused;
        }
        return true;
    }

    /**
     * Stops all pending (and current) auctions and pauses the auction manager
     */
    public void stop() {
        paused = true;
        cancelAuction(activeAuction);
        Auction auction;
        while ((auction = auctions.poll()) != null) {
            cancelAuction(auction, AuctionCancelEvent.CancelCause.MANAGER_STOP, null);
        }
    }

    /**
     * Gets a list of all pending auctions, excluding the active auction (if any)
     *
     * @return the list of pending auctions (excluding current), may be empty but never null
     */
    public List<Auction> getAuctions() {
        List<Auction> auctionList = new ArrayList<Auction>();
        for (Auction auction : auctions.toArray(new Auction[0])) {
            auctionList.add(auction);
        }
        return auctionList;
    }

    /**
     * Gets the currently running auction. May be null if no auction running.
     *
     * @return the current auction, may be null
     */
    public Auction getActiveAuction() {
        return activeAuction;
    }

    /**
     * Determines if this manager is in cooldown. Cooldown is the time between auctions.
     *
     * @return true if the manager is in cooldown
     */
    public boolean isInCooldown() {
        return currentDowntime > 0;
    }

    /**
     * Determines how much of the cooldown is left. This is the time until the next auction begins
     *
     * @return the number of seconds until the next auction starts
     */
    public long getCooldownTimeLeft() {
        return currentDowntime;
    }

    /**
     * Determines if this manager has another auction to grab. If the manager is in cooldown, and there are
     * auctions in the queue, this will return true. If there is an active auction, and there are auctions in
     * the queue, this will return true. If there are no auctions in the queue (regardless if an auction is
     * currently running or not), this will return false.
     *
     * @return true if there is another auction available
     */
    public boolean hasNextAuction() {
        return auctions.size() > 0;
    }

    /**
     * Determines if the manager is paused
     *
     * @return true if the manager is paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Gets the amount of auction time left. This returns -1 if there is no ongoing auction
     *
     * @return the amount of auction time left
     */
    public long getAuctionTimeLeft() {
        if (activeAuction == null) return -1;
        return auctionTimeLeft;
    }

    /**
     * Sets the amount of auction time left. If there is no ongoing auction, this does nothing.
     * If this is passed a "0", the auction is ended on the next tick.
     *
     * @param auctionTimeLeft the number of seconds left for the auction
     * @throws IllegalArgumentException thrown if there is an active auction and the passed time is less than zero
     */
    public void setAuctionTimeLeft(long auctionTimeLeft) {
        if (activeAuction != null) {
            if (auctionTimeLeft < 0) throw new IllegalArgumentException();
            if (this.auctionTimeLeft < auctionTimeLeft) {

            }
            this.auctionTimeLeft = auctionTimeLeft;
        }
    }

    /**
     * Determines if the manager can accept the passed auction. If the passed auction is null,
     * or the seller already has an ongoing (or pending) auction, then this returns false. This
     * will also return false if the AuctionManager is paused
     *
     * @param auction
     * @return
     */
    public boolean canAccept(Auction auction) {
        if (paused) return false;
        if (auction != null) {
            if (auctions.size() < maxQueueSize) {
                for (Auction auction1 : auctions) {
                    if (auction1.getSeller().equalsIgnoreCase(auction.getSeller())) {
                        return false;
                    }
                }
                if (activeAuction != null && activeAuction.getSeller().equalsIgnoreCase(auction.getSeller())) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to submit an auction to the queue. If the auction cannot be submitted or the queue does
     * not accept the auction, this returns false.
     *
     * @param auction the auction to submit
     * @return true if the auction was added to the queue
     */
    public boolean submitAuction(Auction auction) {
        if (canAccept(auction)) {
            if (auctions.offer(auction)) {
                AuctionQueuedEvent event = new AuctionQueuedEvent(auction, getPosition(auction));
                plugin.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled())
                    return true;
                auctions.remove(auction);
            }
        }
        return false;
    }

    /**
     * Gets the maximum queue size for this manager
     *
     * @return the maximum queue size
     */
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    /**
     * Gets the number of auctions currently in queue
     *
     * @return the number of auctions currently in queue
     */
    public int getNumInQueue() {
        return auctions.size();
    }

    /**
     * Gets the queue position of the passed auction. The following status codes are used:
     * <br/>
     * <ul>
     * <li>0 -- <i>Active auction (in progress)</i></li>
     * <li>Greater than or equal to 1 -- <i>Queue position, 1 being "next up"</i></li>
     * <li>Less than 0 -- <i>Error code: Not found or otherwise</i></li>
     * </ul>
     *
     * @param auction the auction to test
     * @return the position of the auction
     * @throws IllegalArgumentException thrown if the passed auction is null
     */
    public int getPosition(Auction auction) {
        if (auction == null) throw new IllegalArgumentException();
        if (activeAuction != null && auction.getSeller().equalsIgnoreCase(activeAuction.getSeller())) {
            return 0;
        }
        int i = 0;
        for (Auction auction1 : auctions) {
            i++;
            if (auction.getSeller().equalsIgnoreCase(auction1.getSeller())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Cancels an auction using magic
     *
     * @param auction the auction to cancel, cannot be null
     * @return true if the auction was cancelled
     */
    public boolean cancelAuction(Auction auction) {
        return cancelAuction(auction, AuctionCancelEvent.CancelCause.MAGIC, null);
    }

    /**
     * Cancels an auction as a specified CommandSender. To cancel for a generic reason, use {@link #cancelAuction(Auction)}
     *
     * @param auction   the auction to cancel, cannot be null
     * @param canceller the canceller, cannot be null
     * @return true if the auction was cancelled
     */
    public boolean cancelAuction(Auction auction, CommandSender canceller) {
        return cancelAuction(auction, AuctionCancelEvent.CancelCause.COMMAND, canceller);
    }

    boolean cancelAuction(Auction auction, AuctionCancelEvent.CancelCause cancelCause, CommandSender sender) {
        boolean found = false;
        boolean isActive = false;
        for (Auction auction1 : auctions) {
            if (auction1.getSeller().equalsIgnoreCase(auction.getSeller())) {
                found = true;
                break;
            }
        }
        if (activeAuction != null && activeAuction.getSeller().equalsIgnoreCase(auction.getSeller())) {
            found = true;
            isActive = true;
        }
        if (!found) return false;
        AuctionCancelEvent event = sender == null && cancelCause != AuctionCancelEvent.CancelCause.COMMAND ? new AuctionCancelEvent(auction, cancelCause) : new AuctionCancelEvent(auction, cancelCause, sender);
        plugin.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (isActive) {
                activeAuction.cancel();
                activeAuction = null;
            } else {
                return auctions.remove(auction);
            }
        }
        return !event.isCancelled();
    }

    // Called from task timer
    void tick() {
        if (paused) return;
        if (currentDowntime > 0 && downtimeTicks > 0) {
            currentDowntime--;
        } else {
            if (activeAuction == null) {
                Auction auction = auctions.poll();
                if (auction != null) {
                    activeAuction = auction;
                    plugin.getServer().getPluginManager().callEvent(new AuctionStartEvent(activeAuction));
                    auctionTimeLeft = activeAuction.getRequiredTime();
                }
            }
            if (activeAuction != null) {
                AuctionTickEvent event = new AuctionTickEvent(activeAuction, auctionTimeLeft);
                plugin.getServer().getPluginManager().callEvent(event);
                auctionTimeLeft = event.getTimeLeft();
                if (auctionTimeLeft < 0) {
                    currentDowntime = downtimeTicks;
                    plugin.getServer().getPluginManager().callEvent(new AuctionEndEvent(activeAuction));
                    AuctionUtil.rewardItems(activeAuction);
                    activeAuction = null;
                }
            }
        }
    }
}
