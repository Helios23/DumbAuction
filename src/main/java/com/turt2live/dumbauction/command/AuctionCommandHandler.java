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

package com.turt2live.dumbauction.command;

import com.turt2live.dumbauction.DumbAuction;
import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.auction.Bid;
import com.turt2live.dumbauction.command.validator.ArgumentValidator;
import com.turt2live.dumbauction.command.validator.DoubleValidator;
import com.turt2live.dumbauction.command.validator.IntValidator;
import com.turt2live.dumbauction.command.validator.InventoryAmountValidator;
import com.turt2live.dumbauction.util.ItemUtil;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the auction command handler
 *
 * @author turt2live
 */
public class AuctionCommandHandler implements CommandExecutor {

    private class CommandInfo {

        Command annotation;
        Method method;

        public CommandInfo(Command annotation, Method method) {
            this.annotation = annotation;
            this.method = method;
        }
    }

    private Map<String, List<CommandInfo>> commands = new HashMap<String, List<CommandInfo>>();
    private DumbAuction plugin;

    public AuctionCommandHandler(DumbAuction plugin) {
        this.plugin = plugin;

        Class<?>[] expectedArguments = new Class[] {
                CommandSender.class,
                Map.class
        };
        for (Method method : getClass().getMethods()) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Command) {
                    if (method.getParameterTypes() == null || (method.getReturnType() != boolean.class && method.getReturnType() != Boolean.class) || method.getParameterTypes().length != expectedArguments.length) {
                        plugin.getLogger().severe("[1] Weird command registration on method " + getClass().getName() + "#" + method.getName());
                        break;
                    } else {
                        boolean valid = true;
                        for (int i = 0; i < expectedArguments.length; i++) {
                            if (expectedArguments[i] != method.getParameterTypes()[i]) {
                                plugin.getLogger().severe("[2] Weird command registration on method " + getClass().getName() + "#" + method.getName());
                                valid = false;
                                break;
                            }
                        }
                        if (!valid) break;
                    }
                    Command auc = (Command) annotation;
                    List<CommandInfo> existing = commands.get(auc.root());
                    if (existing == null) existing = new ArrayList<CommandInfo>();
                    existing.add(new CommandInfo(auc, method));
                    commands.put(auc.root(), existing);
                    break; // We're done here
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("auction")) {
            if (plugin.getMobArena() != null && plugin.getMobArena().isInArena(plugin, (Player) sender)) {
                plugin.sendMessage(sender, ChatColor.RED + "You cannot do that in a MobArena!");
                return true;
            }
            if (!plugin.getConfig().getBoolean("auctions.allow-creative-mode", false) && (sender instanceof Player) && ((Player) sender).getGameMode() == GameMode.CREATIVE) {
                plugin.sendMessage(sender, ChatColor.RED + "You cannot do that in Creative Mode!");
                return true;
            }
        }
        List<CommandInfo> commandHandlers = commands.get(command.getName());
        if (commandHandlers == null || commandHandlers.isEmpty()) {
            plugin.getLogger().severe("No command handler for command: " + command.getName());
            plugin.sendMessage(sender, ChatColor.RED + "Severe internal error. Please contact your administrator.");
            return true;
        }
        if (args.length < 1) {
            args = new String[] {"help"}; // Force help command
        }
        for (CommandInfo handler : commandHandlers) {
            Command annotation = handler.annotation;
            Method method = handler.method;
            if (annotation.subArgument().equalsIgnoreCase(args[0]) || contains(annotation.alternateSubArgs(), args[0])) {
                if (annotation.playersOnly() && !(sender instanceof Player)) {
                    plugin.sendMessage(sender, ChatColor.RED + "You need to be a player to run that command.");
                    return true;
                }
                if (!annotation.permission().equalsIgnoreCase(Command.NO_PERMISSION) && !sender.hasPermission(annotation.permission())) {
                    plugin.sendMessage(sender, ChatColor.RED + "No permission");
                    return true;
                }
                Map<String, Object> arguments = new HashMap<String, Object>();
                int numNonOptional = 0;
                for (Annotation annotation1 : method.getAnnotations()) {
                    if (annotation1 instanceof ArgumentList) {
                        ArgumentList list = (ArgumentList) annotation1;
                        for (Argument arg : list.args()) {
                            if (!arg.optional())
                                numNonOptional++;
                            if (arg.validator() != null) {
                                int realIndex = arg.index() + 1;
                                if (realIndex < args.length) {
                                    try {
                                        ArgumentValidator validator = arg.validator().newInstance();
                                        validator.setArguments(arg.validatorArguments());
                                        String input = args[realIndex];
                                        if (validator.isValid(sender, input)) {
                                            arguments.put(arg.subArgument(), validator.get(sender, input));
                                        } else {
                                            plugin.sendMessage(sender, validator.getErrorMessage(sender, input));
                                            return true;
                                        }
                                    } catch (InstantiationException e) {
                                        e.printStackTrace();
                                        plugin.sendMessage(sender, ChatColor.RED + "Severe internal error. Please contact your administrator.");
                                        return true;
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                        plugin.sendMessage(sender, ChatColor.RED + "Severe internal error. Please contact your administrator.");
                                        return true;
                                    }
                                } else if (!arg.optional()) {
                                    plugin.sendMessage(sender, ChatColor.RED + "Incorrect syntax. Try " + ChatColor.YELLOW + annotation.usage());
                                    return true;
                                }
                            } else {
                                plugin.getLogger().severe("Invalid argument handler for: /" + command.getName() + " " + args[0]);
                                plugin.sendMessage(sender, ChatColor.RED + "Severe internal error. Please contact your administrator.");
                                return true;
                            }
                        }
                    }
                }
                if (numNonOptional > arguments.size()) {
                    plugin.sendMessage(sender, ChatColor.RED + "Incorrect syntax. Try " + ChatColor.YELLOW + annotation.usage());
                    return true;
                }
                try {
                    return (Boolean) method.invoke(this, sender, arguments);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    plugin.sendMessage(sender, ChatColor.RED + "Severe internal error. Please contact your administrator.");
                    return true;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    plugin.sendMessage(sender, ChatColor.RED + "Severe internal error. Please contact your administrator.");
                    return true;
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    plugin.sendMessage(sender, ChatColor.RED + "Severe internal error. Please contact your administrator.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean contains(String[] strings, String arg) {
        for (String s : strings) {
            if (s.equalsIgnoreCase(arg)) return true;
        }
        return false;
    }

    private boolean canPerformCommandInWorld(CommandSender sender) {
        if (!sender.hasPermission("dumbauction.admin")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                List<String> blacklist = plugin.getConfig().getStringList("auctions.excluded-worlds");
                if (blacklist != null && blacklist.contains(player.getWorld().getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "bid",
            usage = "/auc bid [amount]",
            permission = "dumbauction.auction",
            playersOnly = true
    )
    @ArgumentList(args = {
            @Argument(index = 0, optional = true, subArgument = "amount", validator = DoubleValidator.class, validatorArguments = {"Please supply a valid amount!"})
    })
    public boolean auctionBidCommand(CommandSender sender, Map<String, Object> arguments) {
        if (!canPerformCommandInWorld(sender)) {
            plugin.sendMessage(sender, ChatColor.RED + "You cannot do that in this world.");
            return true;
        }

        Auction auction = plugin.getAuctionManager().getActiveAuction();
        if (auction == null) {
            plugin.sendMessage(sender, ChatColor.RED + "There is no active auction!");
            return true;
        }
        double bid = arguments.containsKey("amount") ? (Double) arguments.get("amount") : auction.getNextMinimum();
        if (!plugin.getEconomy().has(sender.getName(), bid)) {
            plugin.sendMessage(sender, ChatColor.RED + "You do not have enough to bid on that!");
            return true;
        }
        if (auction.getRealSeller().equalsIgnoreCase(sender.getName())) {
            plugin.sendMessage(sender, ChatColor.RED + "You can't bid on your own auction!");
            return true;
        }
        if (auction.getHighestBid() != null && auction.getHighestBid().getRealBidder().equalsIgnoreCase(sender.getName())) {
            plugin.sendMessage(sender, ChatColor.RED + "You are already the highest bidder!");
            return true;
        }

        Bid bid1 = new Bid(sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName(), sender.getName(), bid);
        if (!auction.submitBid(bid1)) {
            plugin.sendMessage(sender, ChatColor.RED + "Could not submit bid!");
        }
        // No need to tell them they bid, it will be posted in chat...
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "help",
            alternateSubArgs = {"wat", "how", "?", "wtf"},
            usage = "/auc help",
            permission = Command.NO_PERMISSION
    )
    public boolean auctionHelpCommand(CommandSender sender, Map<String, Object> arguments) {
        if (sender.hasPermission("dumbauction.auction")) {
            plugin.sendMessage(sender, ChatColor.AQUA + "/auc start [amount] [starting price] [bid increment] [time]" + ChatColor.GRAY + " - Starts an auction");
            plugin.sendMessage(sender, ChatColor.AQUA + "/auc info" + ChatColor.GRAY + " - Displays auction information");
            plugin.sendMessage(sender, ChatColor.AQUA + "/auc showqueue" + ChatColor.GRAY + " - Displays the auction queue");
            plugin.sendMessage(sender, ChatColor.AQUA + "/auc cancel" + ChatColor.GRAY + " - Cancels an auction");
            plugin.sendMessage(sender, ChatColor.AQUA + "/auc stfu" + ChatColor.GRAY + " - Toggles auction spam for you only");
            plugin.sendMessage(sender, ChatColor.AQUA + "/auc buy" + ChatColor.GRAY + " - Purchases the auction, if possible");
        }
        if (sender.hasPermission("dumbauction.admin")) {
            plugin.sendMessage(sender, ChatColor.AQUA + "/auc pause" + ChatColor.GRAY + " - Pauses auctions globally");
            plugin.sendMessage(sender, ChatColor.AQUA + "/auc resume" + ChatColor.GRAY + " - Resumes auctions globally");
            plugin.sendMessage(sender, ChatColor.AQUA + "/auc impound" + ChatColor.GRAY + " - Impounds the current auction");
            plugin.sendMessage(sender, ChatColor.AQUA + "/auc reload" + ChatColor.GRAY + " - Reloads the configuration");
        }
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "reload",
            alternateSubArgs = {"rl"},
            usage = "/auc reload",
            permission = "dumbauction.admin"
    )
    public boolean auctionReloadCommand(CommandSender sender, Map<String, Object> arguments) {
        plugin.reloadConfig();
        plugin.sendMessage(sender, ChatColor.GREEN + "Reloaded!");
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "ignore",
            alternateSubArgs = {"toggle", "stfu", "silence", "ignore", "quiet", "off"},
            usage = "/auc toggle",
            permission = "dumbauction.auction"
    )
    public boolean auctionIgnoreCommand(CommandSender sender, Map<String, Object> arguments) {
        plugin.setIgnore(sender.getName(), !plugin.isIgnoring(sender.getName()));
        plugin.sendMessage(sender, ChatColor.AQUA + "You are now " + (plugin.isIgnoring(sender.getName()) ? ChatColor.RED + "ignoring" : ChatColor.GREEN + "not ignoring") + " auction messages");
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "cancel",
            alternateSubArgs = {"stop"},
            usage = "/auc cancel",
            permission = "dumbauction.auction"
    )
    public boolean auctionCancelCommand(CommandSender sender, Map<String, Object> arguments) {
        if (!canPerformCommandInWorld(sender)) {
            plugin.sendMessage(sender, ChatColor.RED + "You cannot do that in this world.");
            return true;
        }

        Auction active = plugin.getAuctionManager().getActiveAuction();
        if (active == null) {
            plugin.sendMessage(sender, ChatColor.RED + "There is no active auction!");
        } else {
            if (active.getRealSeller().equalsIgnoreCase(sender.getName()) || sender.hasPermission("dumbauction.admin")) {
                if (plugin.getAuctionManager().cancelAuction(active, sender))
                    plugin.sendMessage(sender, ChatColor.GREEN + "Auction cancelled.");
                else
                    plugin.sendMessage(sender, ChatColor.RED + "Auction not cancelled");
            } else {
                plugin.sendMessage(sender, ChatColor.RED + "You cannot cancel this auction!");
            }
        }
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "showqueue",
            usage = "/auc showqueue",
            permission = "dumbauction.auction"
    )
    public boolean auctionShowQueueCommand(CommandSender sender, Map<String, Object> arguments) {
        Auction auction = plugin.getAuctionManager().getActiveAuction();
        if (auction != null) {
            ItemStack stack = auction.getTemplateItem().clone();
            stack.setAmount(auction.getItemAmount());
            ItemUtil.showQuickInformation(stack, sender, plugin.getAuctionManager().getPosition(auction));
        }
        for (Auction auction1 : plugin.getAuctionManager().getAuctions()) {
            ItemStack stack = auction1.getTemplateItem().clone();
            stack.setAmount(auction1.getItemAmount());
            ItemUtil.showQuickInformation(stack, sender, plugin.getAuctionManager().getPosition(auction1));
        }
        if (plugin.getAuctionManager().getActiveAuction() == null && plugin.getAuctionManager().getAuctions().size() <= 0) {
            plugin.sendMessage(sender, ChatColor.RED + "There is nothing in the queue.");
        }
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "info",
            alternateSubArgs = {"information", "stats"},
            usage = "/auc info",
            permission = "dumbauction.auction"
    )
    public boolean auctionInfoCommand(CommandSender sender, Map<String, Object> arguments) {
        Auction auction = plugin.getAuctionManager().getActiveAuction();
        if (auction == null) {
            plugin.sendMessage(sender, ChatColor.RED + "No active auction!");
        } else {
            ItemStack stack = auction.getTemplateItem().clone();
            stack.setAmount(auction.getItemAmount());

            String seller = plugin.getConfig().getBoolean("auctions.use-displayname", true) ? auction.getSeller() : auction.getRealSeller();
            String startCost = plugin.getEconomy().format(auction.getMinimumBid());
            String bidIncrement = plugin.getEconomy().format(auction.getBidIncrement());
            String time = plugin.getAuctionManager().getAuctionTimeLeft() + " seconds";
            Bid bid = auction.getHighestBid();

            // Send messages
            plugin.sendMessage(sender, ChatColor.GRAY + "Seller: " + ChatColor.DARK_AQUA + seller);
            plugin.sendMessage(sender, ChatColor.GRAY + "Starting Price: " + ChatColor.AQUA + startCost);
            plugin.sendMessage(sender, ChatColor.GRAY + "Bid Increment: " + ChatColor.AQUA + bidIncrement);
            plugin.sendMessage(sender, ChatColor.GRAY + "Highest Bidder: " + ChatColor.AQUA + (bid == null ? (ChatColor.ITALIC + "no one!") : (plugin.getConfig().getBoolean("auctions.use-displayname", true) ? bid.getBidder() : bid.getRealBidder()) + " (" + plugin.getEconomy().format(bid.getAmount()) + ")"));
            plugin.sendMessage(sender, ChatColor.GRAY + "Time Left: " + ChatColor.GOLD + time);
            plugin.sendMessage(sender, ChatColor.DARK_GREEN + "Item Information: ");
            ItemUtil.showInformation(stack, sender);
        }
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "buy",
            alternateSubArgs = {"buynow"},
            usage = "/auc buy",
            permission = "dumbauction.auction",
            playersOnly = true
    )
    public boolean auctionBuyCommand(CommandSender sender, Map<String, Object> arguments) {
        if (!canPerformCommandInWorld(sender)) {
            plugin.sendMessage(sender, ChatColor.RED + "You cannot do that in this world.");
            return true;
        }

        Player player = (Player) sender; // Validated by @Command
        if (!plugin.getAuctionManager().canBuyNow()) {
            plugin.sendMessage(sender, ChatColor.RED + "Sorry! This auction cannot be purchased at this time.");
        } else {
            if (!plugin.getAuctionManager().buyNow(player)) {
                plugin.sendMessage(sender, ChatColor.RED + "Buying failed. Do you have enough funds?");
            }
        }
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "pause",
            alternateSubArgs = {"stop", "halt", "wait"},
            usage = "/auc pause",
            permission = "dumbauction.admin"
    )
    public boolean auctionPauseCommand(CommandSender sender, Map<String, Object> arguments) {
        if (!plugin.getAuctionManager().isPaused()) {
            plugin.broadcast(ChatColor.RED + sender.getName() + " has paused the auction queue!");
            plugin.getAuctionManager().setPaused(true);
        } else {
            plugin.sendMessage(sender, ChatColor.RED + "Auction queue is already paused");
        }
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "resume",
            alternateSubArgs = {"unpause", "go"},
            usage = "/auc resume",
            permission = "dumbauction.admin"
    )
    public boolean auctionResumeCommand(CommandSender sender, Map<String, Object> arguments) {
        if (plugin.getAuctionManager().isPaused()) {
            plugin.broadcast(ChatColor.GREEN + sender.getName() + " has resumed the auction queue!");
            plugin.getAuctionManager().setPaused(false);
        } else {
            plugin.sendMessage(sender, ChatColor.RED + "Auction queue not paused.");
        }
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "impound",
            alternateSubArgs = {"take", "admin", "seal", "no"},
            usage = "/auc impound",
            permission = "dumbauction.admin",
            playersOnly = true
    )
    public boolean auctionImpoundCommand(CommandSender sender, Map<String, Object> arguments) {
        Player player = (Player) sender; // Validated by @Command
        Auction active = plugin.getAuctionManager().getActiveAuction();
        if (active == null) {
            plugin.sendMessage(sender, ChatColor.RED + "There is no auction in progress!");
            return true;
        }
        plugin.getAuctionManager().impound(active, player);
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "start",
            usage = "/auc start [amount] [startPrice] [increment] [time]",
            playersOnly = true,
            permission = "dumbauction.auction"
    )
    @ArgumentList(args = {
            @Argument(index = 0, subArgument = "amount", optional = true, validator = InventoryAmountValidator.class),
            @Argument(index = 1, subArgument = "startPrice", optional = true, validator = DoubleValidator.class, validatorArguments = {"Please supply a start price"}),
            @Argument(index = 2, subArgument = "bidIncrement", optional = true, validator = DoubleValidator.class, validatorArguments = {"Please supply a bid increment"}),
            @Argument(index = 3, subArgument = "time", optional = true, validator = IntValidator.class, validatorArguments = {"Please supply a valid time"})
    })
    public boolean auctionStartCommand(CommandSender sender, Map<String, Object> arguments) {
        if (!canPerformCommandInWorld(sender)) {
            plugin.sendMessage(sender, ChatColor.RED + "You cannot do that in this world.");
            return true;
        }

        Player player = (Player) sender; // Validated by @Command
        ItemStack hand = player.getItemInHand();
        if (hand == null || hand.getType() == Material.AIR) {
            plugin.sendMessage(sender, ChatColor.RED + "You are not holding anything!");
            return true;
        }

        // First of all, is the queue accepting auctions?
        if (plugin.getAuctionManager().isPaused()) {
            plugin.sendMessage(sender, ChatColor.RED + "Cannot add auction: Queue paused.");
            return true;
        }
        if (plugin.getAuctionManager().isFull()) {
            plugin.sendMessage(sender, ChatColor.RED + "Cannot add auction: Queue full");
            return true;
        }
        if (plugin.getAuctionManager().hasAuction(sender.getName())) {
            plugin.sendMessage(sender, ChatColor.RED + "You already have an auction in the queue!");
            return true;
        }

        // Validate item
        if (!plugin.getConfig().getBoolean("auctions.allow-damaged-items", false) && hand.getType().getMaxDurability() > 0 && hand.getDurability() != 0) {
            plugin.sendMessage(sender, ChatColor.RED + "You cannot auction damaged items");
            return true;
        }
        if (!plugin.getConfig().getBoolean("auctions.allow-renamed-items", false) && hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
            plugin.sendMessage(sender, ChatColor.RED + "You cannot auction renamed items");
            return true;
        }

        // Check blacklist
        List<String> disallowedItems = plugin.getConfig().getStringList("auctions.blacklist");
        if (disallowedItems != null && disallowedItems.size() > 0) {
            for (String bad : disallowedItems) {
                ItemInfo info = Items.itemByName(bad);
                if (info.toStack().isSimilar(hand)) {
                    plugin.sendMessage(sender, ChatColor.RED + "That item cannot be sold!");
                    return true;
                }
            }
        }

        // Check lore/display name blacklist
        List<String> disallowedWords = plugin.getConfig().getStringList("auctions.word-blacklist");
        if (disallowedWords != null && disallowedWords.size() > 0) {
            List<String> lower = new ArrayList<String>();
            for (String word : disallowedWords) lower.add(word.toLowerCase());

            if (hand.hasItemMeta()) {
                ItemMeta meta = hand.getItemMeta();
                if (meta.hasDisplayName()) {
                    for (String bad : lower) {
                        if (ChatColor.stripColor(meta.getDisplayName().toLowerCase()).contains(bad)) {
                            plugin.sendMessage(sender, ChatColor.RED + "That item has words/phrases that are not permitted.");
                            return true;
                        }
                    }
                }
                if (meta.hasLore()) {
                    List<String> lore = meta.getLore();
                    if (lore != null && lore.size() > 0) {
                        for (String listItem : lore) {
                            for (String bad : lower) {
                                if (ChatColor.stripColor(listItem.toLowerCase()).contains(bad)) {
                                    plugin.sendMessage(sender, ChatColor.RED + "That item has words/phrases that are not permitted.");
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Pre-validated variables
        double startPrice = arguments.containsKey("startPrice") ? (Double) arguments.get("startPrice") : plugin.getConfig().getDouble("default-start-price", 100);
        double increment = arguments.containsKey("bidIncrement") ? (Double) arguments.get("bidIncrement") : plugin.getConfig().getDouble("default-bid-increment", 100);
        long time = arguments.containsKey("time") ? (Integer) arguments.get("time") : plugin.getConfig().getLong("default-time-seconds", 30);
        int amount = arguments.containsKey("amount") ? (Integer) arguments.get("amount") : hand.getAmount();

        // Check one : Time limit
        if (time >= plugin.getConfig().getLong("min-auction-time", 10)) {
            if (!player.hasPermission("dumbauction.admin") && time > plugin.getConfig().getLong("max-auction-time", 60)) {
                plugin.sendMessage(sender, ChatColor.RED + "Time too large!");
                return true;
            }
        } else {
            plugin.sendMessage(sender, ChatColor.RED + "Time too small!");
            return true;
        }

        // Check two : Start price
        if (startPrice >= plugin.getConfig().getLong("min-start-cost", 10)) {
            if (!player.hasPermission("dumbauction.admin") && startPrice > plugin.getConfig().getLong("max-start-cost", 20000)) {
                plugin.sendMessage(sender, ChatColor.RED + "Cost too large!");
                return true;
            }
        } else {
            plugin.sendMessage(sender, ChatColor.RED + "Cost too small!");
            return true;
        }

        // Check three : Bid price
        if (increment >= plugin.getConfig().getLong("min-bid-cost", 10)) {
            if (!player.hasPermission("dumbauction.admin") && increment > plugin.getConfig().getLong("max-bid-cost", 20000)) {
                plugin.sendMessage(sender, ChatColor.RED + "Bid increment too large!");
                return true;
            }
        } else {
            plugin.sendMessage(sender, ChatColor.RED + "Bid increment too small!");
            return true;
        }

        // Attempt to add the auction
        Auction auction = new Auction(player.getDisplayName(), sender.getName(), startPrice, increment, time, amount, hand);
        if (plugin.getAuctionManager().submitAuction(auction)) {
            plugin.sendMessage(sender, ChatColor.GREEN + "Your auction has been queued as " + ChatColor.DARK_GREEN + "#" + plugin.getAuctionManager().getPosition(auction));
        } else {
            plugin.sendMessage(sender, ChatColor.RED + "Could not add the auction!");
        }

        return true;
    }
}
