package productLines;


import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import utils.*;
import products.*;
import ingredients.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;

public class testProductLine { 
    private static final String PRODUCT_NAMES[] = { "Product1", "Product2", "Product3", "Product4", "Product5" };
    private static final int STOCK_LEVEL = 12000;;
    private static final int SHELF_LIFE = 20;
    private static final String PARTIAL_DATE = "28/11/";
    private static int NUMBER_OF_FORECASTS = 10;
    private static int year = 10;
    private static final String EG_DATE = "28/11/71";
    private static final int FORECAST_LEVEL = 1000;

    @Test
    public void testProductAddition()
    {
        try {
            ProductLine productLine = new ProductLine();
            for (String productName : PRODUCT_NAMES) 
            {
                Product p = new Product(productName);
                productLine.addProduct(p);
    	    }
            Set<String> productNames = productLine.products();
            for (String productName : productNames)   
            {
                assertTrue(productLine.containsProduct(productName));
                Product p = productLine.getProduct(productName);
                assertEquals(productName, p.name());
            }
        }
        catch (Exception e) {
            fail("unexpected exception");
	}
    }

    @Test
    public void testUnknownProductRequest()
    {
        try {
            ProductLine pl = new ProductLine();
            pl.getProduct(PRODUCT_NAMES[0]);
            fail("Exception expected");
        } catch (UnknownKeyException u) {}
    }

    @Test
    public void testDuplicateProductAddition()
    {
        try {
            ProductLine productLine = new ProductLine();
            Product p = new Product(PRODUCT_NAMES[0]);
            productLine.addProduct(p);
            productLine.addProduct(p);
            fail("exception expected");
        }
        catch (DuplicateKeyException d) {}
    }

    @Test
    public void testIngredientAddition()
    {
        Unit unit = Unit.G;

        try {
            ProductLine productLine = new ProductLine();
            for (String ingredientName : PRODUCT_NAMES) 
            {
                Ingredient i = new Ingredient(ingredientName,unit,SHELF_LIFE);
                productLine.addIngredient(i);
    	    }
            
            Set<String> ingredientNames = productLine.ingredients();
            for (String ingredientName : ingredientNames)   
            {
                Ingredient i = productLine.getIngredient(ingredientName);
                assertEquals(ingredientName, i.name());
                assertEquals(unit,i.unit());
                assertEquals(SHELF_LIFE,i.shelfLife());
            }
        }
        catch (Exception e) {
            fail("unexpected exception");
	}
   }

   @Test
   public void testAddProductForecast()
   {       
       try {
           DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,Locale.UK);

           ProductLine pl = new ProductLine();
           for (String productName : PRODUCT_NAMES)
           {
               pl.addProduct(new Product(productName) );
               for (int i=0; i<NUMBER_OF_FORECASTS; i++)
               {
                  Date date = df.parse(PARTIAL_DATE + (year + i) );
                  pl.addProductForecast(productName, date, i);
               }
               for (int i=0; i<NUMBER_OF_FORECASTS; i++)
               {
                  Date date = df.parse(PARTIAL_DATE + (year + i) );
                  int value = pl.getProductForecast(productName, date);
                  assertEquals(value, i);
               }
           }
       } catch (Exception e) { 
           fail("unexpected exception"); 
      }
   }
   
   @Test
   public void testAddIngredientStockLevel()
   {
        try {
            ProductLine pl = new ProductLine();
            for (String name : PRODUCT_NAMES)
            {
               Ingredient i = new Ingredient(name, Unit.QTY, SHELF_LIFE);
               pl.addIngredient(i);
               pl.addIngredientStockLevel(name, STOCK_LEVEL);
            }
            Set<String> ingredientNames = pl.ingredients();
            for (String iname : ingredientNames )
            {
               assertEquals(pl.getIngredientStockLevel(iname), STOCK_LEVEL);
            } 
        } catch (Exception e) {
            fail("unexpected exception");
        }        
   }

   @Test
   public void TestUninitialisedExceptions()
   {
       try {
           DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,Locale.UK);
           Date aDate;
           aDate = df.parse(EG_DATE);
       
           ProductLine pl = new ProductLine();
           try { 
               pl.addProductForecast(PRODUCT_NAMES[0],aDate,FORECAST_LEVEL);
               fail("exception expected");
           }
           catch (UninitialisedProductException upe) {}
       
           try {
              pl.addIngredientStockLevel(PRODUCT_NAMES[0],STOCK_LEVEL);
              fail("exception expected");
           } 
           catch (UninitialisedIngredientException use) {}
       }
       catch (Exception e) 
       { 
            fail("unexpected exception"); 
       }
   }

   @Test
   public void TestIngredientQuantityCalculationSingleDate()
   {
       final String INGREDIENT_NAME = "anIngredient";
       final int INGREDIENT_QUANTITY_PER_PRODUCT = 1234;
       final Unit A_UNIT = Unit.ML;
       final int PRODUCT_FORECAST = 1000;
       final String A_DATE = "28/11/99";

       try {
           ProductLine productLine = new ProductLine();
           Ingredient i = new Ingredient(INGREDIENT_NAME,A_UNIT);
           productLine.addIngredient(i);

           DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,Locale.UK);
           Date aDate = df.parse(A_DATE);

           for (String productName : PRODUCT_NAMES)
           {
               Product aProduct = new Product(productName);
               aProduct.addIngredientQuantity(INGREDIENT_NAME, INGREDIENT_QUANTITY_PER_PRODUCT);    
       
               productLine.addProduct(aProduct);
               productLine.addProductForecast(productName, aDate, PRODUCT_FORECAST);

               Date forecastDates[] = productLine.getOrderedProductForecastDates(productName);
               Date storedDate = forecastDates[0];

               assertEquals( storedDate, aDate);
               assertEquals( forecastDates.length, 1);
           }

           int expectedResult = INGREDIENT_QUANTITY_PER_PRODUCT * PRODUCT_FORECAST * productLine.products().size();
           assertEquals( expectedResult , productLine.calculateIngredientRequirementByDate(INGREDIENT_NAME, aDate) );

      } catch (Exception e) { 
           fail("unexpected exception"); 
      }
   }

   @Test
   public void TestIngredientDaysCoveredCalculation()
   {
       final String INGREDIENT_NAME = "Another Tasty Ingredient";
       final int INGREDIENT_STOCK_LEVEL = 2505;
       final Unit A_UNIT = Unit.QTY;

       final int PRODUCT_FORECAST = 10;
       final int INGREDIENT_QUANTITY_PER_PRODUCT = 10;

       final String DATE_STRINGS[] = { "28/1/12", "29/1/12", "30/1/12", "31/1/12", "1/2/12", "2/12/12" };
       final int NUM_DATES = DATE_STRINGS.length;

       Date[] dates = new Date[NUM_DATES];

       DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,Locale.UK);
       try {
           for (int i=0; i<NUM_DATES; i++) dates[i] = df.parse(DATE_STRINGS[i]);
       }
       catch (Exception e) {
           fail("unexpected exception" + e.getMessage());
       }

       try {
           ProductLine productLine = new ProductLine();
           Ingredient i = new Ingredient(INGREDIENT_NAME,A_UNIT);
           productLine.addIngredient(i);
           productLine.addIngredientStockLevel(INGREDIENT_NAME, INGREDIENT_STOCK_LEVEL);

           for (String productName : PRODUCT_NAMES)
           {
               Product aProduct = new Product(productName);
               aProduct.addIngredientQuantity(INGREDIENT_NAME, INGREDIENT_QUANTITY_PER_PRODUCT);
               productLine.addProduct(aProduct);

               for (Date aDate : dates)
               {
                   productLine.addProductForecast(productName, aDate, PRODUCT_FORECAST);
               }
           }

           double numberOfDaysCovered = productLine.calculateNumberOfDaysIngredientsWillCover(INGREDIENT_NAME);

           int dailyRequirement = (productLine.products().size() * PRODUCT_FORECAST * INGREDIENT_QUANTITY_PER_PRODUCT);

           double expectedResult = (double)INGREDIENT_STOCK_LEVEL / (double)dailyRequirement;
           if (expectedResult > (double)NUM_DATES) 
           {
              expectedResult = (double)NUM_DATES;
           }

           DecimalFormat toOneDecimalPlace = new DecimalFormat("#.#");
           expectedResult = (double) (Double.valueOf(toOneDecimalPlace.format(expectedResult)));

           assertEquals ( (int)(numberOfDaysCovered*10), (int)(expectedResult*10) );
       }
       catch (Exception e) {
           fail("unexpected exception: " + e.getMessage());
       }
    }
}
