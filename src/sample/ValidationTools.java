package sample;

/** Helpful tools for validating user input into the generator. Implemented on a need-basis.
 * @see GeneratorController*/
public class ValidationTools {

    /** Checks if the input is a numerical value and is greater than 0. Throws exception if there is a problem,
     * otherwise does nothing.
     * @throws InvalidInputException if value is left blank, less than 0 or non-numerical.
     * @param argName The name of the entry field for producing an alert error message.
     * @param value The value to be tested.*/
    public static void CheckNumericAboveZero(String argName, String value) throws InvalidInputException {
        if (value.length() == 0) {
            throw new InvalidInputException("Please enter a value for " + argName + " greater than 0 (e.g. 5).");
        }
        try {
            if (Integer.parseInt(value) <= 0) {
                throw new InvalidInputException("Please enter a value for " + argName + " that is greater than 0 (e.g. 5).");
            }
        }
        catch (NumberFormatException e) {
            throw new InvalidInputException("Please enter a valid number for " + argName + " (e.g. 5).");
        }
    }

    /** Checks if the input is a numerical value. Throws exception if there is a problem,
     * otherwise does nothing.
     * @throws InvalidInputException if value is left blank and/or non-numerical.
     * @param argName The name of the entry field for producing an alert error message.
     * @param value The value to be tested.*/
    public static void CheckNumeric(String argName, String value) throws InvalidInputException {
        if (value.length() == 0) {
            throw new InvalidInputException("Please enter a value for " + argName + " (e.g. 5).");
        }

        try {
            Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new InvalidInputException("Please enter a valid number for " + argName + " (e.g. 5).");
        }
    }
}
