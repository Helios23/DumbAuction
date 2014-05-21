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

package com.turt2live.dumbauction.rewards;

import com.turt2live.dumbauction.DumbAuction;
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

/**
 * Represents an item store for offline players
 *
 * @author turt2live
 */
public class OfflineStore implements SavingStore, Listener {

    private Map<UUID, List<ItemStack>> queue = new HashMap<UUID, List<ItemStack>>();
    private File file;

    public OfflineStore(DumbAuction plugin) throws IOException, InvalidConfigurationException {
        this(plugin, "offline.yml");
    }

    protected OfflineStore(DumbAuction plugin, String fileName) throws IOException, InvalidConfigurationException {
        file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) file.createNewFile();

        FileConfiguration config = new YamlConfiguration();
        config.load(file);
        if (config.getKeys(false) != null) {
            for (String playerUuid : config.getKeys(false)) {
                List<ItemStack> items = new ArrayList<ItemStack>();
                for (String key : config.getConfigurationSection(playerUuid).getKeys(false)) {
                    items.add(config.getItemStack(playerUuid + "." + key));
                }
                queue.put(UUID.fromString(playerUuid), items);
            }
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void save() {
        try {
            FileConfiguration config = new YamlConfiguration();
            config.load(file);
            Set<String> configKeys = config.getKeys(false);
            for (String key : configKeys) {
                config.set(key, null);
            }
            for (UUID key : queue.keySet()) {
                List<ItemStack> items = queue.get(key);
                for (int i = 0; i < items.size(); i++) {
                    config.set(key.toString() + "." + i, items.get(i));
                }
            }
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void store(UUID uuid, ItemStack item) {
        if (item == null || uuid == null) throw new IllegalArgumentException();
        List<ItemStack> stacks = queue.get(uuid);
        if (stacks == null) stacks = new ArrayList<ItemStack>();
        stacks.add(item);
        queue.put(uuid, stacks);
    }

    @Override
    public boolean distributeStore(UUID player, Player distributeTo) {
        if (player == null) throw new IllegalArgumentException();
        if (distributeTo == null) distributeTo = DumbAuction.getInstance().getServer().getPlayer(player);
        List<ItemStack> queue = getStore(player);
        if (queue != null) {
            clearStore(player);
            Map<Integer, ItemStack> overflow = distributeTo.getInventory().addItem(queue.toArray(new ItemStack[0]));
            if (overflow != null && !overflow.isEmpty()) {
                Location drop = distributeTo.getLocation();
                for (ItemStack item : overflow.values()) {
                    drop.getWorld().dropItemNaturally(drop, item);
                }
                DumbAuction.getInstance().sendMessage(distributeTo, ChatColor.RED + "" + ChatColor.BOLD + "Some of your auctions went on the ground - Full inventory");
            }
            DumbAuction.getInstance().sendMessage(distributeTo, ChatColor.GREEN + "Winnings from auctions you bid on were given to you.");
            return true;
        }
        return false;
    }

    @Override
    public void store(UUID player, List<ItemStack> items) {
        if (items == null || player == null) throw new IllegalArgumentException();
        for (ItemStack stack : items) store(player, stack);
    }

    @Override
    public void clearStore(UUID player) {
        if (player == null) throw new IllegalArgumentException();
        queue.remove(player);
    }

    @Override
    public boolean isApplicable(UUID player) {
        if (player == null) return false;
        return DumbAuction.getInstance().getServer().getPlayer(player) == null;
    }

    @Override
    public List<ItemStack> getStore(UUID player) {
        if (player == null) throw new IllegalArgumentException();
        return queue.get(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        distributeStore(event.getPlayer().getUniqueId(), event.getPlayer());
    }
}
