package com.turt2live.dumbauction.command;

import com.turt2live.dumbauction.DumbAuction;
import com.turt2live.dumbauction.command.validator.ArgumentValidator;
import com.turt2live.dumbauction.command.validator.DoubleValidator;
import com.turt2live.dumbauction.command.validator.IntValidator;
import com.turt2live.dumbauction.command.validator.InventoryAmountValidator;
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

    /*@Command(
            root = "auction",
            subArgument = "test",
            usage = "/auc test"
    )
    @ArgumentList(args = {
            @Argument(index = 0, subArgument = "test2", optional = false),
            @Argument(index = 1, subArgument = "test3", optional = true)
    })
    public boolean testCommand(CommandSender sender, Map<String, Object> args) {
        plugin.sendMessage(sender, ChatColor.AQUA + "Test command.");
        for (String key : args.keySet()) {
            plugin.sendMessage(sender, key + " : " + args.get(key));
        }
        return true;
    }*/

    @Command(
            root = "auction",
            subArgument = "start",
            usage = "/auc start [amount] [startPrice] [increment] [time]",
            playersOnly = true
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

        // Pre-validated variables
        double startPrice = arguments.containsKey("startPrice") ? (Double) arguments.get("startPrice") : plugin.getConfig().getDouble("default-start-price", 100);
        double increment = arguments.containsKey("bidIncrement") ? (Double) arguments.get("bidIncrement") : plugin.getConfig().getDouble("default-bid-increment", 100);
        long time = arguments.containsKey("time") ? (Integer) arguments.get("time") : plugin.getConfig().getLong("default-time-seconds", 30);
        int amount = arguments.containsKey("amount") ? (Integer) arguments.get("amount") : hand.getAmount();

        // TODO: Rest of command
        return true;
    }
}
