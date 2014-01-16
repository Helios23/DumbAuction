package com.turt2live.dumbauction.auction;

import org.bukkit.scheduler.BukkitRunnable;

class AuctionTask extends BukkitRunnable {

    private AuctionManager manager;

    AuctionTask(AuctionManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        manager.tick();
    }
}
