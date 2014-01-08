package com.turt2live.dumbauction;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Auction {

    private String seller;
    private String highBidder;
    private double highBid;
    private double bidIncrement;
    private double startAmount;
    private long secondsLeft;
    private List<ItemStack> items;
    private Map<String, Double> bids = new HashMap<String, Double>();
    private boolean firstTick = true;

    public Auction(String seller, double bidIncrement, double startAmount, long time, List<ItemStack> items) {
        this.seller = seller;
        this.bidIncrement = bidIncrement;
        this.startAmount = startAmount;
        this.secondsLeft = time;
        this.items = items;
    }

    public String getHighBidder() {
        return highBidder;
    }

    public double getHighBid() {
        return highBid;
    }

    public boolean hasBids() {
        return bids.size() > 0;
    }

    public String getSeller() {
        return seller;
    }

    public double getBidIncrement() {
        return bidIncrement;
    }

    public double getStartAmount() {
        return startAmount;
    }

    public long getSecondsLeft() {
        return secondsLeft;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void extendTime(long extension) {
        this.secondsLeft += extension;
    }

    public boolean bid(String bidder, double amount) {
        if (!hasBids() || (amount > highBid && amount - highBid >= bidIncrement)) {
            highBid = amount;
            highBidder = bidder;
            bids.put(bidder, amount);
            DumbAuction.p.broadcast(ChatColor.GREEN + bidder + ChatColor.AQUA + " has bid " + ChatColor.GREEN + DumbAuction.economy.format(amount));
            // TODO: Sniping
            return true;
        }
        return false;
    }

    // cause fuck security
    public void tick() {
        if (secondsLeft > 0 && (secondsLeft % 60 == 0 || secondsLeft == 30 || secondsLeft == 15 || secondsLeft <= 3 || firstTick)) {
            DumbAuction.p.broadcast(ChatColor.YELLOW + "" + secondsLeft + " seconds left!");
            firstTick = false;
        }
        secondsLeft--;
    }

    public void reward() {
        DumbAuction plugin = DumbAuction.p;
        if (bids.size() > 0) {
            String name = DumbAuction.getName(items.get(0).getType());
            for (ItemStack stack : items) {
                ItemMeta meta = stack.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    name = ChatColor.ITALIC + meta.getDisplayName();
                }
            }
            // TODO: Offline mode winnings?
            Player player = plugin.getServer().getPlayerExact(highBidder);
            if (player != null) {
                HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(items.toArray(new ItemStack[0]));
                if (overflow != null && overflow.size() > 0) {
                    Location dropLocation = player.getLocation().clone();
                    dropLocation = dropLocation.add(0, 1, 0);
                    for (ItemStack stack : overflow.values()) {
                        player.getWorld().dropItemNaturally(dropLocation, stack);
                    }
                    plugin.sendMessage(player, ChatColor.RED + "Your inventory was full, some items were dropped on the ground.");
                }
                player.updateInventory();
                plugin.sendMessage(player, ChatColor.GREEN + "You won the auction!");
            }
            plugin.broadcast(ChatColor.GREEN + player.getName() + " has won the auction for " + ChatColor.YELLOW + name + ChatColor.GREEN + " at " + ChatColor.YELLOW + DumbAuction.economy.format(highBid));

            // xfer money
            DumbAuction.economy.withdrawPlayer(highBidder, highBid);
            DumbAuction.economy.depositPlayer(seller, highBid);
            return;
        }
        // Else, no bidders
        if (secondsLeft <= 0) plugin.broadcast(ChatColor.GRAY + "Auction ended with no bids.");
        Player player = plugin.getServer().getPlayerExact(seller);
        if (player != null) {
            HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(items.toArray(new ItemStack[0]));
            if (overflow != null && overflow.size() > 0) {
                Location dropLocation = player.getLocation().clone();
                dropLocation = dropLocation.add(0, 1, 0);
                for (ItemStack stack : overflow.values()) {
                    player.getWorld().dropItemNaturally(dropLocation, stack);
                }
                plugin.sendMessage(player, ChatColor.RED + "Your inventory was full, some items were dropped on the ground.");
            }
            player.updateInventory();
            plugin.sendMessage(player, ChatColor.GREEN + "Your items have been returned to you.");
        }
    }

    public void onStart() {
        int total = 0;
        String name = DumbAuction.getName(items.get(0).getType());
        for (ItemStack stack : items) {
            total += stack.getAmount();
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                name = ChatColor.ITALIC + meta.getDisplayName();
            }
        }
        firstTick = true;
        DumbAuction.p.broadcast(ChatColor.GOLD + seller + ChatColor.YELLOW + " is selling " + ChatColor.GOLD + "" + total + "x " + name + " " + ChatColor.YELLOW + "for " + ChatColor.GOLD + secondsLeft + " seconds");
        DumbAuction.p.broadcast(ChatColor.GRAY + "Starting Price: " + ChatColor.AQUA + DumbAuction.economy.format(startAmount) + ChatColor.GRAY + " Bid Increment: " + ChatColor.AQUA + DumbAuction.economy.format(bidIncrement));
    }

    public void info(CommandSender sender, boolean quick) {
        int total = 0;
        String name = DumbAuction.getName(items.get(0).getType());
        for (ItemStack stack : items) {
            total += stack.getAmount();
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                name = ChatColor.ITALIC + meta.getDisplayName();
            }
        }

        // quick ? showqueue : info
        if (quick) {
            // TODO: Prepend auction place in queue
            DumbAuction.p.sendMessage(sender, ChatColor.GOLD + seller + ChatColor.YELLOW + " is selling " + ChatColor.GOLD + "" + total + "x " + name);
        } else {
            // TODO: real item name, enchants, meta, time left, etc
            DumbAuction.p.sendMessage(sender, ChatColor.GOLD + seller + ChatColor.YELLOW + " is selling " + ChatColor.GOLD + "" + total + "x " + name);
            DumbAuction.p.sendMessage(sender, ChatColor.GRAY + "Starting Price: " + ChatColor.AQUA + DumbAuction.economy.format(startAmount) + ChatColor.GRAY + " Bid Increment: " + ChatColor.AQUA + DumbAuction.economy.format(bidIncrement));
            if (highBidder != null) {
                DumbAuction.p.sendMessage(sender, ChatColor.GREEN + highBidder + " has the high bid at " + DumbAuction.economy.format(highBid));
            } else {
                DumbAuction.p.sendMessage(sender, ChatColor.GREEN + "No bids!");
            }
        }
    }

    public void cancel(AuctionManager manager) {
        this.highBid = 0;
        this.highBidder = null;
        this.bids.clear();
        manager.cancel(this);
        DumbAuction.p.broadcast(ChatColor.YELLOW + "The auction has been cancelled!");
    }
}