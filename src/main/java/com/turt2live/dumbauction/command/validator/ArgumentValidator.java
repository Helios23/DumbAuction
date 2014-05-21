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
 * Represents an argument validator
 *
 * @author turt2live
 */
public interface ArgumentValidator {

    /**
     * Determines if the specified input is valid
     *
     * @param input  the input string
     * @param sender the comamnd sender
     *
     * @return true if the input is valid
     */
    public boolean isValid(CommandSender sender, String input);

    /**
     * Gets the value representing the specified input
     *
     * @param input  the input
     * @param sender the comamnd sender
     *
     * @return the output, or null if invalid input
     */
    public Object get(CommandSender sender, String input);

    /**
     * Gets an error message for the supplied input
     *
     * @param input  the input
     * @param sender the command sender. Although passed, error messages should not be displayed to this sender
     *
     * @return the error message
     */
    public String getErrorMessage(CommandSender sender, String input);

    /**
     * Sets the arguments to be used by this validator
     *
     * @param arguments the arguments to use
     */
    public void setArguments(String[] arguments);

}
