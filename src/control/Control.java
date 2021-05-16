package control;

import utils.*;
import parsers.*;
import ingredients.*;
import productLines.*;
import parsers.*;

import java.util.*;
import java.lang.*;

import java.lang.System;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class Control
{
    private static String[] readLinesAndStripComments(FileReader fileReader)
    throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;

        while ( (line = bufferedReader.readLine()) != null ) 
        {
            if ( (line.length() == 0) || ( ! line.startsWith(new String("#")) ) )
            {
               lines.add(line);
            }
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }    

    private static void showUsage()
    {
        System.err.println("Usage: java <program-name> <stock-level-file-name> <product-ingredients-list-file-name> <product-forecast-list-file-name>\n");
        System.exit(1);
    }

    private static void openFileError(Exception e) 
    {
        System.err.println("Error opening file(s): " + e.getMessage() );
        showUsage();
    }

    private static void doCalculations(String ingName, ProductLine pl)
    {
        try {
            double numberOfDays = pl.calculateNumberOfDaysIngredientsWillCover(ingName);
            System.out.printf("Ingredient <%s> Stock Level <%d>\n", ingName, pl.getIngredientStockLevel(ingName));
            System.out.printf("Ingredient <%s> Forecast <%.1f> days of stock\n", ingName, numberOfDays);
            System.out.printf("***\n");
        }
        catch (Exception e) { ProgrammingError.abort(e.getMessage()); };
    }

    public static void main(String args[])
    {
        if (args.length != 3) showUsage();

        FileReader stockLevelfr = null;
        FileReader ingredientsListfr = null;
        FileReader productForecastfr = null;

        String slFile = args[0];
        String ilFile = args[1];
        String pfFile = args[2];

        ProductLine productLine = new ProductLine();

        try {
            stockLevelfr = new FileReader(slFile);
            ingredientsListfr = new FileReader(ilFile);
            productForecastfr = new FileReader(pfFile);
        }
        catch (IOException ioe) {
            openFileError(ioe);
        }

        String[] currentFile = null;
        try {
            String stockLevel[] = readLinesAndStripComments(stockLevelfr);
            String ingredientsList[] = readLinesAndStripComments(ingredientsListfr);
            String productForecast[] = readLinesAndStripComments(productForecastfr);

            currentFile = stockLevel;
            IngredientStockLevelParser islp = new IngredientStockLevelParser(slFile, stockLevel, productLine);
            islp.doParse();

            currentFile = ingredientsList;
            IngredientsListParser ilp = new IngredientsListParser(ilFile, ingredientsList, productLine);
            ilp.doParse();

            currentFile = productForecast;
            ProductForecastParser pfp = new ProductForecastParser(pfFile, productForecast, productLine);
            pfp.doParse();

            for (String ing : productLine.ingredients())
            {
                doCalculations(ing, productLine);
            }
        }
        catch (IOException ioe)
        {
           openFileError(ioe);
        }
        catch (ParseError pe)
        {
           System.err.println(pe.getMessage());
           System.err.printf("Line %d: <%s>\n", pe.line(), currentFile[pe.line()]);
           System.exit(1);
        }
        System.exit(0);
    }
}

