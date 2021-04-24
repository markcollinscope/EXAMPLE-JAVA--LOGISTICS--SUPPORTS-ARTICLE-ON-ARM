package parsers;

import productLines.*;
import ingredients.*;
import products.*;
import utils.*;
import java.util.*;

public class IngredientStockLevelParser
{
    private String fileName;
    private String[] fileLines;
    private ProductLine productLine;   
    int count = 0;
    
    public IngredientStockLevelParser(String file, String[] lines, ProductLine pl)
    {
        fileName = file;
        fileLines = lines;
        productLine = pl;
    }

    public void doParse()
    throws ParseError
    {
        while (count < fileLines.length)
        {
            Scanner s = new Scanner(fileLines[count]).useDelimiter(",");
            int quantity = -1;
            String unitName = "";
            String ingredientName = "";

            try {
                ingredientName = s.next();
                unitName = s.next();
                quantity = s.nextInt();
            }
            catch (Exception e)
            {
                throw new ParseError(fileName, count+1, "Syntax error - expected <ingredient name>,<unit>,<number>");
            }
            
            Unit u = Unit.UNDEFINED;
            try {
                u = Unit.unit(unitName);
                quantity *= Unit.multiplicationFactor(unitName);
            }
            catch (UnknownKeyException uke) {
                throw new ParseError(fileName, count+1, "Unknown unit value found: " + unitName);
            }

            try {
               productLine.addIngredient(new Ingredient(ingredientName, u));
            }
            catch (DuplicateKeyException dke) {
                throw new ParseError(
                    fileName, 
                    count+1, 
                    "Duplicate ingredient: " + ingredientName + " - an ingredient can only appear once in a stock list"
                );
            }
            
            try {
               productLine.addIngredientStockLevel(ingredientName, quantity);
            }
            catch (Exception e) {
                ProgrammingError.abort("Can't add ingredient stock level");
            }
            count++;           
        }
    }
}
