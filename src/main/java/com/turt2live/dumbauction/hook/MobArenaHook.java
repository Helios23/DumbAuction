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

package com.turt2live.dumbauction.hook;

import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import com.turt2live.dumbauction.DumbAuction;
import com.turt2live.dumbauction.rewards.OfflineStore;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.UUID;

public class MobArenaHook extends OfflineStore implements Listener {

    private final String enableNode = "hooks.mobarena.protect";
    private MobArenaHandler handler;

    public MobArenaHook() throws IOException, InvalidConfigurationException {
        super(DumbAuction.getInstance(), "mobarena.yml");
        // Events registered in super (OfflineStore)
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
                distributeStore(event.getPlayer().getUniqueId(), null); // Message handled internally
            }
        }, 5L);
    }

    @Override
    public boolean isApplicable(UUID player) {
        if (DumbAuction.getInstance().getServer().getPlayer(player) == null) return false;
        return isInArena(DumbAuction.getInstance(), DumbAuction.getInstance().getServer().getPlayer(player));
    }
}
