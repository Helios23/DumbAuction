package com.turt2live.dumbauction;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class OfflineQueue implements Listener {

    private Map<String, List<ItemStack>> queue = new HashMap<String, List<ItemStack>>();
    private File file;

    public OfflineQueue(DumbAuction plugin) throws IOException {
        file = new File(plugin.getDataFolder(), "offline.yml");
        if (!file.exists()) file.createNewFile();

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.getKeys(false) != null) {
            for (String playerName : config.getKeys(false)) {
                List<?> objs = config.getList(playerName);
                if (objs != null && objs.size() > 0 && objs.get(0) instanceof ItemStack) {
                    queue.put(playerName, (List<ItemStack>) objs);
                }
            }
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void save() throws IOException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Set<String> keys = new HashSet<String>(config.getKeys(false));
        for (String key : keys) {
            if (!queue.containsKey(key)) {
                config.set(key, null);
            } else {
                config.set(key, queue.get(key));
            }
        }
        config.save(file);
    }

    public void addToQueue(String playerName, ItemStack item) {
        List<ItemStack> stacks = queue.get(playerName);
        if (stacks == null) stacks = new ArrayList<ItemStack>();
        stacks.add(item);
        queue.put(playerName, stacks);
    }

    public void clearQueue(String playerName) {
        queue.remove(playerName);
    }

    public List<ItemStack> getQueue(String playerName) {
        return queue.get(playerName);
    }

    public boolean hasQueue(String playerName) {
        return getQueue(playerName) != null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> queue = getQueue(player.getName());
        if (queue != null) {
            clearQueue(player.getName());
            Map<Integer, ItemStack> overflow = player.getInventory().addItem(queue.toArray(new ItemStack[0]));
            if (overflow != null && !overflow.isEmpty()) {
                Location drop = player.getLocation();
                for (ItemStack item : overflow.values()) {
                    drop.getWorld().dropItemNaturally(drop, item);
                }
                DumbAuction.p.sendMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "Some of your auctions went on the ground - Full inventory");
            }
            DumbAuction.p.sendMessage(player, ChatColor.GREEN + "Winnings from auctions you bid on were given to you.");
        }
    }

}
