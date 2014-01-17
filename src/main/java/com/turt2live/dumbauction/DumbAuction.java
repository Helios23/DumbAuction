package com.turt2live.dumbauction;

import com.turt2live.commonsense.DumbPlugin;
import com.turt2live.dumbauction.auction.AuctionManager;
import com.turt2live.dumbauction.command.AuctionCommandHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DumbAuction extends DumbPlugin {

    /*
    TODO: Missing Features
    > Logging to file
    > Creative mode blocking
    > Damaged items deny
    > Global stfu
    > Banned items
    > Deposit tax to specified user account
    > Prevent gm change
    > Sealed bidding (silent auction)
    > Auction house
    > Ability to disable specified lore/display name messages
    > Impound (admin cancel)
    > Buy now

    TODO: Other stuff
    > Internal listener for lang and auction events (reserve items and such)
    > Implementation of new code
    > MobArena hook re-implement
    > relllleeeassseeee
     */

    private static DumbAuction p;

    private Economy economy;
    private AuctionManager auctions;
    private List<String> ignoreBroadcast = new ArrayList<String>();
    private WhatIsItHook whatHook;
    private OfflineQueue queue;
    private MobArenaHook maHook;

    @Override
    public void onEnable() {
        p = this;
        saveDefaultConfig();
        if (!setupEconomy()) {
            getLogger().severe("A Vault-supported economy plugin was not found. Please add one to your server.");
            getLogger().severe("For a simple economy plugin, I suggest DumbCoin: http://dev.bukkit.org/bukkit-plugins/dumbcoin/");
            getServer().getPluginManager().disablePlugin(p);
            return;
        }
        initCommonSense(72073);

        getCommand("auction").setExecutor(new AuctionCommandHandler(this));
        getCommand("bid").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                String[] strings1 = new String[strings.length + 1];
                strings[0] = "bid";
                for (int i = 0; i < strings.length; i++) {
                    strings1[i + 1] = strings[i];
                }
                return getCommand("auction").getExecutor().onCommand(commandSender, getCommand("auction"), s, strings1);
            }
        });

        getServer().getPluginManager().registerEvents(new InternalListener(), this);

        auctions = new AuctionManager();

        if (getServer().getPluginManager().getPlugin("WhatIsIt") != null) {
            whatHook = new WhatIsItHook();
        }

        if (getServer().getPluginManager().getPlugin("MobArena") != null) {
            maHook = new MobArenaHook();
        }

        ignoreBroadcast = getConfig().getStringList("ignore-broadcast");
        if (ignoreBroadcast == null) {
            ignoreBroadcast = new ArrayList<String>();
        }

        try {
            queue = new OfflineQueue(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        p = null;
        if (auctions != null) auctions.stop(); // Returns items
        if (queue != null) try {
            queue.save();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

   /* @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("auction")) {
            if (!sender.hasPermission("dumbauction.auction")) {
                sendMessage(sender, ChatColor.RED + "No permission.");
            } else {
                if (sender instanceof Player) {
                    if (maHook != null && maHook.isInArena(this, (Player) sender)) {
                        sendMessage(sender, ChatColor.RED + "You cannot do that in a MobArena!");
                        return true;
                    }
                    if (args.length < 1) {
                        sendMessage(sender, ChatColor.RED + "Incorrect syntax. Did you mean " + ChatColor.YELLOW + "/auc <start | info | showqueue | cancel | toggle | bid>" + ChatColor.RED + "?");
                    } else {
                        if (args[0].equalsIgnoreCase("bid")) {
                            if (ignoreBroadcast.contains(sender.getName())) {
                                sendMessage(sender, ChatColor.RED + "You must be listening to auctions to do that.");
                                return true;
                            }
                            Auction auction = auctions.getActiveAuction();
                            if (auction == null) {
                                sendMessage(sender, ChatColor.RED + "There is no active auction.");
                                return true;
                            }
                            if (auction.getSeller().equalsIgnoreCase(sender.getName())) {
                                sendMessage(sender, ChatColor.RED + "You cannot bid on your own auction");
                                return true;
                            }
                            if (auction.getHighBidder() != null && auction.getHighBidder().equalsIgnoreCase(sender.getName())) {
                                sendMessage(sender, ChatColor.RED + "You are already the high bidder!");
                                return true;
                            }
                            try {
                                double bid = args.length > 1 ? Double.parseDouble(args[1]) : (auction.hasBids() ? auction.getHighBid() + auction.getBidIncrement() : auction.getStartAmount());
                                if (DumbAuction.economy.has(sender.getName(), bid)) {
                                    if (!auction.bid(sender.getName(), bid)) {
                                        sendMessage(sender, ChatColor.RED + "Invalid bid! Please see the increment!");
                                    }
                                } else {
                                    sendMessage(sender, ChatColor.RED + "You cannot afford that!");
                                }
                            } catch (NumberFormatException e) {
                                sendMessage(sender, ChatColor.RED + "Invalid number!");
                            }
                        } else {
                            sendMessage(sender, ChatColor.RED + "Incorrect syntax. Did you mean " + ChatColor.YELLOW + "/auc <start | info | showqueue | cancel | toggle | bid>" + ChatColor.RED + "?");
                        }
                    }
                } else {
                    sendMessage(sender, ChatColor.RED + "No");
                }
            }
        } else if (command.getName().equalsIgnoreCase("bid")) {
            String[] newArgs = new String[args.length + 1];
            newArgs[0] = "bid";
            for (int i = 0; i < newArgs.length - 1; i++) {
                newArgs[i + 1] = args[i];
            }
            return onCommand(sender, getCommand("auction"), "auction", newArgs);
        } else {
            sendMessage(sender, ChatColor.RED + "Something broke.");
        }
        return true;
    }*/

    public OfflineQueue getQueue() {
        return queue;
    }

    public AuctionManager getAuctionManager() {
        return auctions;
    }

    public MobArenaHook getMobArena() {
        return maHook;
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage((ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", ChatColor.GRAY + "[DumbAuction]")) + " " + ChatColor.WHITE + message).trim());
    }

    public void broadcast(String message) {
        for (Player player : getServer().getOnlinePlayers()) {
            if (!ignoreBroadcast.contains(player.getName())) {
                sendMessage(player, message);
            }
        }
        sendMessage(getServer().getConsoleSender(), message);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public void setIgnore(String player, boolean ignore) {
        if (ignore) ignoreBroadcast.add(player);
        else ignoreBroadcast.remove(player);
    }

    public boolean isIgnoring(String player) {
        return ignoreBroadcast.contains(player);
    }

    /**
     * Gets the active instance of DumbAuction
     *
     * @return the plugin instance
     */
    public static DumbAuction getInstance() {
        return p;
    }

    /**
     * Gets the WhatIsIt Hook, if present
     *
     * @return the hook, or null if not loaded
     */
    public WhatIsItHook getWhatIsIt() {
        return whatHook;
    }

    /**
     * Gets the Vault economy hook
     *
     * @return Vault economy hook
     */
    public Economy getEconomy() {
        return economy;
    }

}
