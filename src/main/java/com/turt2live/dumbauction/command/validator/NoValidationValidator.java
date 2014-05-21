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

import org.bukkit.command.CommandSender;

/**
 * A validator that does no validation
 *
 * @author turt2live
 */
public class NoValidationValidator implements ArgumentValidator {

    @Override
    public boolean isValid(CommandSender sender, String input) {
        return true;
    }

    @Override
    public Object get(CommandSender sender, String input) {
        return input;
    }

    @Override
    public String getErrorMessage(CommandSender sender, String input) {
        return "";
    }

    @Override
    public void setArguments(String[] arguments) {
    }
}
