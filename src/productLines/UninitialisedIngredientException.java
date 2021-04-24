package productLines;

public class UninitialisedIngredientException extends Exception {
    UninitialisedIngredientException(String msg)
    {
        super("Ingredient must be added to ProductLine before it can be used:" +  msg);
    }
}

