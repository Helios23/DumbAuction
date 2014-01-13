package com.turt2live.dumbauction.command.validator;

/**
 * A validator that does no validation
 *
 * @author turt2live
 */
public class NoValidationValidator implements ArgumentValidator {

    @Override
    public boolean isValid(String input) {
        return true;
    }

    @Override
    public Object get(String input) {
        return input;
    }

    @Override
    public String getErrorMessage(String input) {
        return "";
    }
}
