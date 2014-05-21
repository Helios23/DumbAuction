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

package com.turt2live.dumbauction.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to register auction commands
 *
 * @author turt2live
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Command {

    /**
     * A constant to be used for "no permission needed"
     */
    public static final String NO_PERMISSION = "";

    /**
     * The command root, as defined in the plugin.yml
     *
     * @return the command root
     */
    String root();

    /**
     * The sub argument (eg: "start" in "/auc start")
     *
     * @return the sub argument
     */
    String subArgument();

    /**
     * Alternate sub arguments
     *
     * @return alternate sub arguments
     */
    String[] alternateSubArgs() default {};

    /**
     * The permission needed to run this sub-command. If no permission is needed, use {@link #NO_PERMISSION}
     *
     * @return the permission needed to run this command
     */
    String permission() default NO_PERMISSION;

    /**
     * The usage string of this command
     *
     * @return the usage string
     */
    String usage();

    /**
     * Defines whether or not only players can use this command
     *
     * @return true if players can only use this command
     */
    boolean playersOnly() default false;
}
