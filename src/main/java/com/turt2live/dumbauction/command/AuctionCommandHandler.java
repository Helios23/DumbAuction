package com.turt2live.dumbauction.command;

import com.turt2live.dumbauction.DumbAuction;
import com.turt2live.dumbauction.auction.Auction;
import com.turt2live.dumbauction.command.validator.ArgumentValidator;
import com.turt2live.dumbauction.command.validator.DoubleValidator;
import com.turt2live.dumbauction.command.validator.IntValidator;
import com.turt2live.dumbauction.command.validator.InventoryAmountValidator;
import com.turt2live.dumbauction.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        Class<?>[] expectedArguments = new Class[]{
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
        List<CommandInfo> commandHandlers = commands.get(command.getName());
        if (commandHandlers == null || commandHandlers.isEmpty()) {
            plugin.getLogger().severe("No command handler for command: " + command.getName());
            plugin.sendMessage(sender, ChatColor.RED + "Severe internal error. Please contact your administrator.");
            return true;
        }
        if (args.length < 1) {
            plugin.sendMessage(sender, ChatColor.RED + "Did you mean " + ChatColor.YELLOW + "/" + command.getName() + " help" + ChatColor.RED + "?");
            return true;
        }
        for (CommandInfo handler : commandHandlers) {
            Command annotation = handler.annotation;
            Method method = handler.method;
            if (annotation.subArgument().equalsIgnoreCase(args[0])) {
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
                if (numNonOptional < arguments.size()) {
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

    @Command(
            root = "auction",
            subArgument = "cancel",
            usage = "/auc cancel",
            permission = "dumbauction.auction"
    )
    public boolean auctionCancelCommand(CommandSender sender, Map<String, Object> arguments) {
        Auction active = plugin.getAuctionManager().getActiveAuction();
        if (active == null) {
            plugin.sendMessage(sender, ChatColor.RED + "There is no active auction!");
        } else {
            if (active.getSeller().equalsIgnoreCase(sender.getName()) || sender.hasPermission("dumbauction.admin")) {
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
        return true;
    }

    @Command(
            root = "auction",
            subArgument = "info",
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
            ItemUtil.showInformation(stack, sender);
        }
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

        // Generate a list of items we are taking
        List<ItemStack> taking = new ArrayList<ItemStack>();
        int taken = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || !itemStack.isSimilar(hand)) continue;
            int newTaken = taken + itemStack.getAmount();
            ItemStack stack = itemStack.clone();
            if (newTaken > amount) {
                stack.setAmount(amount - taken);
                newTaken = amount;
            }
            taking.add(stack);
            taken = newTaken;
            if (taken > amount) break;
        }

        // Attempt to add the auction
        Auction auction = new Auction(sender.getName(), startPrice, increment, time, amount, hand);
        if (plugin.getAuctionManager().submitAuction(auction)) {
            plugin.sendMessage(sender, ChatColor.GREEN + "Your auction has been queued as " + ChatColor.DARK_GREEN + "#" + plugin.getAuctionManager().getPosition(auction));
        } else {
            plugin.sendMessage(sender, ChatColor.RED + "Could not add the auction!");
        }

        return true;
    }
}
