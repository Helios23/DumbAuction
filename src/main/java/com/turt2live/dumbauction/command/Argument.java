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

import com.turt2live.dumbauction.command.validator.ArgumentValidator;
import com.turt2live.dumbauction.command.validator.NoValidationValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to define an argument for a command
 *
 * @author turt2live
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Argument {

    /**
     * The argument index where index is the number in this example: "/auc start 0 1 2 3"
     *
     * @return the argument index
     */
    int index() default 0;

    /**
     * Flags the argument as optional
     *
     * @return true if optional
     */
    boolean optional() default false;

    /**
     * The argument name
     *
     * @return
     */
    String subArgument();

    /**
     * The argument validator
     *
     * @return the argument validator
     */
    Class<? extends ArgumentValidator> validator() default NoValidationValidator.class;

    /**
     * Arguments for the validator
     *
     * @return the arguments
     */
    String[] validatorArguments() default {};
}
