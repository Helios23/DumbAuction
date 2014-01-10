package com.turt2live.dumbauction;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class AuctionManager extends BukkitRunnable {

    private DumbAuction plugin = DumbAuction.p;
    private int max = plugin.getConfig().getInt("max-queue-size", 3);
    private ArrayBlockingQueue<Auction> auctions = new ArrayBlockingQueue<Auction>(max);
    private long downtimeTicks = plugin.getConfig().getLong("seconds-between-auctions", 15);
    private long currentDowntime = 0;
    private boolean locked = false;
    private Auction activeAuction;

    public AuctionManager() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 20L, 20L);
    }

    public int getQueuePosition(String seller) {
        if (activeAuction != null && activeAuction.getSeller().equalsIgnoreCase(seller)) {
            return 0;
        }
        int i = 1;
        for (Auction auc : auctions) {
            if (auc.getSeller().equalsIgnoreCase(seller)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public boolean addAuction(Auction auction) {
        if (locked) return false;
        if (auction != null) {
            if (auctions.offer(auction)) {
                currentDowntime = 0; // Reset
                return true;
            }
        }
        return false;
    }

    public Auction getActiveAuction() {
        return activeAuction;
    }

    public List<Auction> getAuctions() {
        List<Auction> auctionList = new ArrayList<Auction>();
        for (Auction auction : auctions.toArray(new Auction[0])) {
            auctionList.add(auction);
        }
        return auctionList;
    }

    public void cancel(Auction auction) {
        if (activeAuction != null && auction.getSeller().equalsIgnoreCase(activeAuction.getSeller())) {
            currentDowntime = downtimeTicks;
            activeAuction.reward();
            activeAuction = null;
        }
    }

    public void stop() {
        cancel();
        locked = true;
        Auction auction;
        if (activeAuction != null) activeAuction.reward();
        while ((auction = auctions.poll()) != null) {
            auction.reward(); // Will return items
        }
    }

    public void run() {
        if (currentDowntime > 0) {
            currentDowntime--;
        } else {
            if (activeAuction == null) {
                Auction auction = auctions.poll();
                if (auction != null) {
                    activeAuction = auction;
                    activeAuction.onStart();
                }
            }
            if (activeAuction != null) {
                activeAuction.tick();
                if (activeAuction.getSecondsLeft() < 0) {
                    currentDowntime = downtimeTicks;
                    activeAuction.reward();
                    activeAuction = null;
                }
            }
        }
    }

    public int size() {
        return auctions.size();
    }
}
