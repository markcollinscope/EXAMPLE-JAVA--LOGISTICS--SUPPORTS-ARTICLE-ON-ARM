package productLines;

public class UninitialisedProductException extends Exception {
    UninitialisedProductException(String msg)
    {
        super("Product must be added to ProductLine before it can be used:" +  msg);
    }
}

