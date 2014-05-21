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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a store registry
 *
 * @author turt2live
 */
public class StoreRegistry {

    private List<RewardStore> stores = new ArrayList<RewardStore>();

    /**
     * Adds a reward store to the registry
     *
     * @param store the store to add
     */
    public void addStore(RewardStore store) {
        if (store == null) throw new IllegalArgumentException();
        this.stores.add(store);
    }

    /**
     * Removes a store from the registry
     *
     * @param store the store to remove
     */
    public void removeStore(RewardStore store) {
        if (store != null) {
            this.stores.remove(store);
        }
    }

    /**
     * Determines the most applicable store for the player. This returns the first found store
     *
     * @param player the player to look for, cannot be null
     *
     * @return the applicable store, or null if none found
     */
    public RewardStore getApplicableStore(UUID player) {
        if (player == null) throw new IllegalArgumentException();
        for (RewardStore store : stores) {
            if (store.isApplicable(player)) {
                return store;
            }
        }
        return null;
    }

    /**
     * Saves all applicable stores
     */
    public void save() {
        for (RewardStore store : stores) {
            if (store instanceof SavingStore) {
                ((SavingStore) store).save();
            }
        }
    }

}
