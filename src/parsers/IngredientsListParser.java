package parsers;

import productLines.*;
import ingredients.*;
import products.*;
import utils.*;
import java.util.*;

public class IngredientsListParser
{
    private String fileName;
    private String[] fileLines;
    private ProductLine productLine;   
    int count = 0;
    
    public IngredientsListParser(String file, String[] lines, ProductLine pl)
    {
        fileName = file;
        fileLines = lines;
        productLine = pl;
    }

    private Product doParseProductNameAndAddToProductLine()
    throws ParseError
    {
        String productName = fileLines[count];
        if (productName.length() == 0)
        { 
            throw new ParseError(fileName, count+1, "a product name was expected but not found");
        }

        Product p = new Product(productName);
        try {
            productLine.addProduct(p);
        }
        catch (DuplicateKeyException dke) {
           throw new ParseError(
              fileName, 
              count+1, 
              "Product:" + productName + " found twice - ingredients lists may only be defined once");
        }
        count++;
        return p;
    }

    private void doParseIngredientsListAndAddToProduct(Product product)
    throws ParseError
    {
        while ((count < fileLines.length) && !fileLines[count].equals("") )
        {
            Scanner s = new Scanner(fileLines[count]).useDelimiter(",");
            int quantity = -1;
            String unitName = "";
            String ingredientName = "";

            try {
                quantity = s.nextInt();
                unitName = s.next();
                ingredientName = s.next();
            }
            catch (Exception e)
            {
                throw new ParseError(fileName, count+1, "Syntax error - expected <number>,<unit>,<ingredient name>");
            }

            Unit u = Unit.UNDEFINED;
            try {
                u = Unit.unit(unitName);
                quantity *= Unit.multiplicationFactor(unitName);
            } 
            catch (UnknownKeyException uke) {
                throw new ParseError(fileName, count+1, "Syntax error - unknown unit specified: " + unitName);
            }

            try {
               Ingredient i = productLine.getIngredient(ingredientName);
               if (i.unit() != u) 
               {
                    throw new ParseError(
                    fileName, 
                    count+1, 
                    "ingredient: " + ingredientName + " quantity measurement unit does not match ingredient as defined in Ingredient Stock Level File");
               }
            } catch (UnknownKeyException uke) {
                throw new ParseError(
                    fileName, 
                    count+1, 
                    "Unknown ingredient: " + ingredientName + " - ingredients must first be defined in the Ingredient Stock Level file");
            }

            try {
               product.addIngredientQuantity(ingredientName, quantity);
            }
            catch (DuplicateKeyException dke) {
                throw new ParseError(
                    fileName, 
                    count+1, 
                    "Duplicate ingredient: " + ingredientName + " - an ingredient can only appear once in a Product Ingredient List");
            }                 
            count++;
         }
         
         count++;
    }

    public void doParse()
    throws ParseError
    {
        while (count < fileLines.length)
        {
            Product newProduct = doParseProductNameAndAddToProductLine();
            doParseIngredientsListAndAddToProduct(newProduct);
        }    
    }
}
