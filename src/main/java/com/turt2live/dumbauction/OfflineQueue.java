package com.turt2live.dumbauction;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
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

    public OfflineQueue(DumbAuction plugin) throws IOException, InvalidConfigurationException {
        file = new File(plugin.getDataFolder(), "offline.yml");
        if (!file.exists()) file.createNewFile();

        FileConfiguration config = new YamlConfiguration();
        config.load(file);
        if (config.getKeys(false) != null) {
            for (String playerName : config.getKeys(false)) {
                List<ItemStack> items = new ArrayList<ItemStack>();
                for (String key : config.getConfigurationSection(playerName).getKeys(false)) {
                    items.add(config.getItemStack(playerName + "." + key));
                }
                queue.put(playerName, items);
            }
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void save() throws IOException, InvalidConfigurationException {
        FileConfiguration config = new YamlConfiguration();
        config.load(file);
        Set<String> configKeys = config.getKeys(false);
        for (String key : configKeys) {
            config.set(key, null);
        }
        for (String key : queue.keySet()) {
            List<ItemStack> items = queue.get(key);
            for (int i = 0; i < items.size(); i++) {
                config.set(key + "." + i, items.get(i));
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
                DumbAuction.getInstance().sendMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "Some of your auctions went on the ground - Full inventory");
            }
            DumbAuction.getInstance().sendMessage(player, ChatColor.GREEN + "Winnings from auctions you bid on were given to you.");
        }
    }

}
