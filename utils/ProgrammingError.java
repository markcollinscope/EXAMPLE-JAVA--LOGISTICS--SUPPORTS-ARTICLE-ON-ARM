package utils;

import java.lang.IllegalStateException;

public class ProgrammingError
{
    public static void abort(String msg)
    {
        System.err.printf("Programming Error - %s", msg);
        throw new IllegalStateException(msg);
    }
}
