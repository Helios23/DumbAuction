package com.turt2live.dumbauction.auction;

import com.turt2live.dumbauction.DumbAuction;
import com.turt2live.dumbauction.event.AuctionCancelEvent;
import com.turt2live.dumbauction.event.AuctionQueuePauseStateEvent;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Represents the auction manager. This handles all the ticking of auctions and the flow
 * of said auctions.
 *
 * @author turt2live
 */
public class AuctionManager {

    private DumbAuction plugin = DumbAuction.getInstance();
    private int max = plugin.getConfig().getInt("max-queue-size", 3);
    private long downtimeTicks = plugin.getConfig().getLong("seconds-between-auctions", 15);
    private ArrayBlockingQueue<Auction> auctions = new ArrayBlockingQueue<Auction>(max);
    private long currentDowntime = 0;
    private boolean locked = false;
    private Auction activeAuction;
    private boolean paused = false;
    private AuctionTask task;

    public AuctionManager() {
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
     * Stops all pending auctions and locks the auction manager
     */
    public void stop() {
        locked = true;
        cancelAuction(activeAuction);
        Auction auction = null;
        while ((auction = auctions.poll()) != null) {
            cancelAuction(auction);
        }
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
        AuctionCancelEvent event = sender == null && cancelCause != AuctionCancelEvent.CancelCause.COMMAND ? new AuctionCancelEvent(auction, cancelCause) : new AuctionCancelEvent(auction, cancelCause, sender);
        plugin.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()){
            activeAuction=null;
        }
        return !event.isCancelled();
    }

    // Called from task timer
    void tick() {

    }
}
