package com.turt2live.dumbauction.command.validator;

/**
 * Represents an argument validator
 *
 * @author turt2live
 */
public interface ArgumentValidator {

    /**
     * Determines if the specified input is valid
     *
     * @param input the input string
     * @return true if the input is valid
     */
    public boolean isValid(String input);

    /**
     * Gets the value representing the specified input
     *
     * @param input the input
     * @return the output, or null if invalid input
     */
    public Object get(String input);

    /**
     * Gets an error message for the supplied input
     *
     * @param input the input
     * @return the error message
     */
    public String getErrorMessage(String input);

}
