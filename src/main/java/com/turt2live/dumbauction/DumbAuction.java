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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    > Internal listener for lang and auction events
    > Implementation of new code
    > relllleeeassseeee
     */

    public static DumbAuction p;
    public static Economy economy;

    private List<String> toggles = new ArrayList<String>();
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

        auctions = new AuctionManager();
        toggles.add("toggle");
        toggles.add("stfu");
        toggles.add("silence");
        toggles.add("ignore");
        toggles.add("quiet");
        toggles.add("off");

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
                        if (args[0].equalsIgnoreCase("info")) {
                            if (ignoreBroadcast.contains(sender.getName())) {
                                sendMessage(sender, ChatColor.RED + "You must be listening to auctions to do that.");
                                return true;
                            }
                            Auction auction = auctions.getActiveAuction();
                            if (auction == null) {
                                sendMessage(sender, ChatColor.RED + "There is no active auction.");
                                return true;
                            }
                            auction.info(sender, false);
                        } else if (args[0].equalsIgnoreCase("showqueue")) {
                            if (ignoreBroadcast.contains(sender.getName())) {
                                sendMessage(sender, ChatColor.RED + "You must be listening to auctions to do that.");
                                return true;
                            }
                            List<Auction> queue = auctions.getAuctions();
                            if (queue.size() > 0) {
                                for (Auction auction : queue) {
                                    auction.info(sender, true);
                                }
                            } else {
                                sendMessage(sender, ChatColor.RED + "No queued auctions!");
                            }
                        } else if (args[0].equalsIgnoreCase("cancel")) {
                            if (ignoreBroadcast.contains(sender.getName())) {
                                sendMessage(sender, ChatColor.RED + "You must be listening to auctions to do that.");
                                return true;
                            }
                            Auction auction = auctions.getActiveAuction();
                            if (auction == null) {
                                sendMessage(sender, ChatColor.RED + "There is no active auction.");
                                return true;
                            }
                            if (auction.getSeller().equalsIgnoreCase(sender.getName()) || sender.hasPermission("dumbauction.admin")) {
                                auction.cancel(auctions);
                            } else {
                                sendMessage(sender, ChatColor.RED + "Not your auction!");
                            }
                        } else if (toggles.contains(args[0].toLowerCase())) {
                            if (ignoreBroadcast.contains(sender.getName())) ignoreBroadcast.remove(sender.getName());
                            else ignoreBroadcast.add(sender.getName());
                            getConfig().set("ignore-broadcast", ignoreBroadcast);
                            sendMessage(sender, ChatColor.YELLOW + "You are now " + (ignoreBroadcast.contains(sender.getName()) ? (ChatColor.RED + "IGNORING") : (ChatColor.GREEN + "NOT IGNORING")) + ChatColor.YELLOW + " auctions.");
                        } else if (args[0].equalsIgnoreCase("bid")) {
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
                        } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                            if (sender.hasPermission("dumbauction.admin")) {
                                saveConfig();
                                reloadConfig();
                            } else {
                                sendMessage(sender, ChatColor.RED + "You do not have permission to do that.");
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

    private void showAucHelp(CommandSender sender, int n) {
        String base = ChatColor.RED + "Incorrect syntax. Did you mean " + ChatColor.YELLOW + "/auc start";
        if (n >= 2) base += " [amount]";
        if (n >= 3) base += " [starting price]";
        if (n >= 4) base += " [bid increment]";
        if (n >= 5) base += " [time]";
        base += ChatColor.RED + "?";
        sendMessage(sender, base);
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

    public static String getName(ItemStack stack) {
        String def = stack.getType().name();
        if (p.whatHook != null) {
            def = p.whatHook.getName(stack);
        } else {
            if (p.getConfig().getString("aliases." + stack.getType().name()) != null) {
                def = p.getConfig().getString("aliases." + stack.getType().name());
            }
        }

        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta.hasDisplayName()) {
                def = meta.getDisplayName();
            }
        }

        String[] parts = def.split("_");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            builder.append(parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase());
            builder.append(" ");
        }
        return builder.toString().trim();
    }

    public static void displayItem(CommandSender sender, ItemStack stack, int amount) {
        p.sendMessage(sender, ChatColor.BLUE + getName(stack) + ChatColor.BLUE + " x" + amount);
        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta.hasDisplayName()) {
                String def = stack.getType().name();
                if (p.whatHook != null) {
                    def = p.whatHook.getName(stack);
                } else {
                    if (p.getConfig().getString("aliases." + stack.getType().name()) != null) {
                        def = p.getConfig().getString("aliases." + stack.getType().name());
                    }
                }
                String[] parts = def.split("_");
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < parts.length; i++) {
                    builder.append(parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase());
                    builder.append(" ");
                }
                def = builder.toString().trim();
                p.sendMessage(sender, ChatColor.DARK_AQUA + "Real Item Type: " + def);
            }
            if (meta.hasLore()) {
                p.sendMessage(sender, ChatColor.LIGHT_PURPLE + "Lore: ");
                for (String s : meta.getLore()) {
                    p.sendMessage(sender, "  " + s);
                }
                p.sendMessage(sender, " ");
            }
        }
        if (stack.getEnchantments() != null && !stack.getEnchantments().isEmpty()) {
            Map<Enchantment, Integer> enchants = stack.getEnchantments();
            for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                String enchantName = Items.getEnchantmentName(e);
                p.sendMessage(sender, ChatColor.AQUA + enchantName);
            }
        }
    }

    /**
     * Gets the active instance of DumbAuction
     *
     * @return the plugin instance
     */
    public static DumbAuction getInstance() {
        return p;
    }
}
