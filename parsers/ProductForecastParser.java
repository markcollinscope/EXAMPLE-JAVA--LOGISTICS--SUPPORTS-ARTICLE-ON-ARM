package parsers;

import productLines.*;
import ingredients.*;
import products.*;
import utils.*;
import java.util.*;
import java.lang.*;

import java.text.*;


public class ProductForecastParser
{
    private String fileName;
    private String[] fileLines;
    private ProductLine productLine;   
    int count = 0;
    private ArrayList<Date> forecastDates = new ArrayList<Date>();
    private boolean verbose = false;
    
    public ProductForecastParser(String file, String[] lines, ProductLine pl)
    {
        fileName = file;
        fileLines = lines;
        productLine = pl;
    }

    private int doParseOfDates()
    throws ParseError
    {
        Scanner s = new Scanner(fileLines[count]).useDelimiter(",");
        while (s.hasNext())
        {
            try {
                String dateString = s.next();
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,Locale.UK);
                Date aDate = df.parse(dateString);
                forecastDates.add(aDate);
            }
            catch (ParseException p) {
                throw new ParseError(fileName, count+1, "Syntax Error - Invalid Date Format - " + p.getMessage());
            }

        }
        count++;
        return forecastDates.size();
    }

    private void doParseOfProductSalesForecastLines(int expectedNumberOfValues)
    throws ParseError
    {
        while (count < fileLines.length)
        {
            Scanner s = new Scanner(fileLines[count]).useDelimiter(",");
            String productName = "";

            try {
                productName = s.next();
            }
            catch (NoSuchElementException nsee) {
                throw new ParseError(fileName, count+1, "Expected to find Product Name");
            }

            if (!productLine.containsProduct(productName))
            {
                throw new ParseError(fileName, count+1, "Unknown Product: " + productName + " - products must first be defined in the Product Ingredients file");
            }

            int days = 0;
            try {
                while (s.hasNext())
                {
                    int forecastQuantity = s.nextInt();

                    productLine.addProductForecast(productName, forecastDates.get(days), forecastQuantity);
                    days++;
                }
            }
            catch (Exception e) {
                throw new ParseError(fileName, count+1, "Syntax Error - Expected <Product name>,<value>,<value>, ..." + e.getMessage());
            }
            
            if (days != expectedNumberOfValues)
            {
                throw new ParseError(fileName, count+1, "Too few or too many forecast values - expected " + expectedNumberOfValues + " found " + days);
            }
            count++;
        }
    }

    public void doParse()
    throws ParseError
    {
        int nForecastsExpected = doParseOfDates();
        doParseOfProductSalesForecastLines(nForecastsExpected);
    }
}
