package com.turt2live.dumbauction;

import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobArenaHook implements Listener {

    private final String enableNode = "hooks.mobarena.protect";
    private MobArenaHandler handler;
    private Map<String, List<ItemStack>> winnings = new HashMap<String, List<ItemStack>>();
    private Map<String, List<ItemStack>> failed = new HashMap<String, List<ItemStack>>();

    public MobArenaHook() {
        DumbAuction.p.getServer().getPluginManager().registerEvents(this, DumbAuction.p);
    }

    public boolean isInArena(DumbAuction plugin, Player player) {
        if (plugin.getConfig().getBoolean(enableNode, true)) {
            if (handler == null) handler = new MobArenaHandler();
            return handler.isPlayerInArena(player);
        }
        return false;
    }

    public void queue(Player player, List<ItemStack> items, boolean win) {
        List<ItemStack> existing = (win ? winnings : failed).get(player.getName());
        if (existing == null) existing = new ArrayList<ItemStack>();
        existing.addAll(items);
        (win ? winnings : failed).put(player.getName(), existing);
    }

    @EventHandler
    public void onLeave(final ArenaPlayerLeaveEvent event) {
        DumbAuction.p.getServer().getScheduler().scheduleSyncDelayedTask(DumbAuction.p, new Runnable() {
            @Override
            public void run() {
                List<ItemStack> items = winnings.get(event.getPlayer().getName());
                if (items != null) {
                    Auction.rewardItems(event.getPlayer(), items, true);
                }
                items = failed.get(event.getPlayer().getName());
                if (items != null) {
                    Auction.rewardItems(event.getPlayer(), items, false);
                }
                winnings.remove(event.getPlayer().getName());
                failed.remove(event.getPlayer().getName());
            }
        }, 5L);
    }
}
