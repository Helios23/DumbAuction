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

package com.turt2live.dumbauction.command.validator;

import com.turt2live.dumbauction.DumbAuction;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Validates arguments to be integers
 *
 * @author turt2live
 */
public class IntValidator implements ArgumentValidator {

    private String error;

    @Override
    public boolean isValid(CommandSender sender, String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    @Override
    public Integer get(CommandSender sender, String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    @Override
    public String getErrorMessage(CommandSender sender, String input) {
        return ChatColor.RED + error;
    }

    @Override
    public void setArguments(String[] arguments) {
        if (arguments.length < 1)
            throw new IllegalArgumentException("requires 1 string argument");
        if (arguments.length > 1)
            DumbAuction.getInstance().getLogger().warning("Extra arguments supplied in IntValidator");
        error = arguments[0];
    }
}
