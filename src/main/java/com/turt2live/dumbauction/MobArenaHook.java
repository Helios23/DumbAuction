package com.turt2live.dumbauction;

import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import com.turt2live.dumbauction.rewards.OfflineStore;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class MobArenaHook extends OfflineStore implements Listener {

    private final String enableNode = "hooks.mobarena.protect";
    private MobArenaHandler handler;

    public MobArenaHook() throws IOException, InvalidConfigurationException {
        super(DumbAuction.getInstance(), "mobarena.yml");
        // Events registered in super
    }

    public boolean isInArena(DumbAuction plugin, Player player) {
        if (plugin.getConfig().getBoolean(enableNode, true)) {
            if (handler == null) handler = new MobArenaHandler();
            return handler.isPlayerInArena(player);
        }
        return false;
    }

    @EventHandler
    public void onLeave(final ArenaPlayerLeaveEvent event) {
        DumbAuction.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(DumbAuction.getInstance(), new Runnable() {
            @Override
            public void run() {
                distributeStore(event.getPlayer().getName(), null); // Message handled internally
            }
        }, 5L);
    }

    @Override
    public boolean isApplicable(String player) {
        if (DumbAuction.getInstance().getServer().getPlayerExact(player) == null) return false;
        return isInArena(DumbAuction.getInstance(), DumbAuction.getInstance().getServer().getPlayerExact(player));
    }
}
