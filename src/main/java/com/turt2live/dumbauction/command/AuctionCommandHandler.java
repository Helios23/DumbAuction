package com.turt2live.dumbauction.command;

import com.turt2live.dumbauction.DumbAuction;
import com.turt2live.dumbauction.command.validator.ArgumentValidator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        AuctionCommand annotation;
        Method method;

        public CommandInfo(AuctionCommand annotation, Method method) {
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
            System.out.println(method.getName());
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                System.out.println(annotation.toString());
                if (annotation instanceof AuctionCommand) {
                    if (method.getParameterTypes() == null || method.getReturnType() != Boolean.class || method.getParameterTypes().length != expectedArguments.length) {
                        plugin.getLogger().severe("Weird command registration on method " + getClass().getName() + "#" + method.getName());
                        break;
                    } else {
                        boolean valid = true;
                        for (int i = 0; i < expectedArguments.length; i++) {
                            if (expectedArguments[i] != method.getParameterTypes()[i]) {
                                plugin.getLogger().severe("Weird command registration on method " + getClass().getName() + "#" + method.getName());
                                valid = false;
                                break;
                            }
                        }
                        if (!valid) break;
                    }
                    AuctionCommand auc = (AuctionCommand) annotation;
                    List<CommandInfo> existing = commands.get(auc.root());
                    if (existing == null) existing = new ArrayList<CommandInfo>();
                    existing.add(new CommandInfo(auc, method));
                    commands.put(auc.root(), existing);
                    break; // We're done here
                }
            }
        }
    }

    // TODO: This doesn't work :(
    // https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/plugin/java/JavaPluginLoader.java#L252
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        List<CommandInfo> commandHandlers = commands.get(command.getName());
        if (commandHandlers == null || commandHandlers.isEmpty()) {
            plugin.getLogger().severe("No command handler for command: " + command.getName());
            sender.sendMessage(ChatColor.RED + "Severe internal error. Please contact your administrator.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Did you mean " + ChatColor.YELLOW + "/" + command.getName() + " help" + ChatColor.RED + "?");
            return true;
        }
        for (CommandInfo handler : commandHandlers) {
            AuctionCommand annotation = handler.annotation;
            Method method = handler.method;
            if (annotation.subArgument().equalsIgnoreCase(args[0])) {
                if (annotation.playersOnly() && !(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You need to be a player to run that command.");
                    return true;
                }
                Map<String, Object> arguments = new HashMap<String, Object>();
                for (Annotation annotation1 : method.getAnnotations()) {
                    if (annotation1 instanceof AuctionArgument) {
                        AuctionArgument arg = (AuctionArgument) annotation1;
                        if (arg.validator() != null) {
                            int realIndex = arg.index() + 1;
                            if (realIndex < args.length) {
                                try {
                                    ArgumentValidator validator = arg.validator().newInstance();
                                    String input = args[realIndex];
                                    if (validator.isValid(input)) {
                                        arguments.put(arg.subArgument(), validator.get(input));
                                    } else {
                                        sender.sendMessage(validator.getErrorMessage(input));
                                        return true;
                                    }
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                    sender.sendMessage(ChatColor.RED + "Severe internal error. Please contact your administrator.");
                                    return true;
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                    sender.sendMessage(ChatColor.RED + "Severe internal error. Please contact your administrator.");
                                    return true;
                                }
                            } else if (!arg.optional()) {
                                sender.sendMessage(annotation.usage());
                                return true;
                            }
                        } else {
                            plugin.getLogger().severe("Invalid argument handler for: /" + command.getName() + " " + args[0]);
                            sender.sendMessage(ChatColor.RED + "Severe internal error. Please contact your administrator.");
                            return true;
                        }
                    }
                }
                try {
                    return (Boolean) method.invoke(sender, arguments);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "Severe internal error. Please contact your administrator.");
                    return true;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "Severe internal error. Please contact your administrator.");
                    return true;
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "Severe internal error. Please contact your administrator.");
                    return true;
                }
            }
        }
        return false;
    }

    @AuctionCommand(
            root = "auction",
            subArgument = "test",
            usage = "/auc test"
    )
    @AuctionArgument(index = 0, subArgument = "test2", optional = true)
    public boolean testCommand(CommandSender sender, Map<String, Object> args) {
        sender.sendMessage(ChatColor.AQUA + "Test command. Supplied optional? " + (args.containsKey("test2") ? ChatColor.GREEN + "Yes: " + args.get("test2") : ChatColor.RED + "No"));
        return true;
    }
}
