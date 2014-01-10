package com.turt2live.dumbauction;

import com.garbagemule.MobArena.MobArenaHandler;
import org.bukkit.entity.Player;

public class MobArenaHook {

    private final String enableNode = "hooks.mobarena.disable-auction-command-in-arena";
    private MobArenaHandler handler;

    public boolean isInArena(DumbAuction plugin, Player player) {
        if (plugin.getConfig().getBoolean(enableNode, true)) {
            if (handler == null) handler = new MobArenaHandler();
            return handler.isPlayerInArena(player);
        }
        return false;
    }

}
