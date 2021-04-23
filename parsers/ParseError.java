package parsers;

public class ParseError extends Exception
{
   private String file;
   private int line;
   private String errorMsg;

   ParseError(String file, int line, String errorMsg)
   {
       super("File: " + file + " Line: " + line + " Error: " + errorMsg);
       this.file = file;
       this.line = line;
       this.errorMsg = errorMsg;
   }

   public String file() 
   { 
       return file; 
   }
   public int line() 
   { 
      return line; 
   }
   public String errorMsg()
   {
      return errorMsg;
   }
}

