package parsers;

import utils.*;
import ingredients.*;
import products.*;
import productLines.*;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class testParsers
{
    // cooking lesson.
    private static final String BASE_INGREDIENTS_NAME = "Minced Beef";
    private static final String BASE_PRODUCT_NAME = "Chilli Con Carne";
    private static final String FILE_NAME = "ChilliRecipes";

    private String[] createIngredients(int numberOfIngredients)
    {
        String[] ingredientNames = new String[numberOfIngredients];
        for (int i=0; i<numberOfIngredients; i++)
        {
            ingredientNames[i] = new String(BASE_INGREDIENTS_NAME + i);
        }
        return ingredientNames;
    }

    private String[] createIngredientsAndAddToProductLine(int numberOfIngredients, ProductLine pl, Unit unitIngredients)
    {
        String[] ingredientNames = createIngredients(numberOfIngredients);
        for (String iname : ingredientNames)
        {
            try {
                pl.addIngredient(new Ingredient(iname, unitIngredients));
            }
            catch (DuplicateKeyException dke) {
                fail("unexpected exception");
            }
        }
        return ingredientNames;
    }

    private String[] createProductNames(int numberOfProducts)
    {
        String[] productNames = new String[numberOfProducts];
        for (int i=0; i<numberOfProducts; i++)
        {
            productNames[i] = new String(BASE_PRODUCT_NAME + i);
        }
        return productNames;
    }

    private String createIngredientLine(String ingredientName, String quantityAsString, String unitAsString)
    {
        return new String(quantityAsString + "," + unitAsString + "," + ingredientName);
    }

    private String[] buildProductIngredientsListInputFile(String[] productNames, String ingredientNames[], String quantityAsString, String unitAsString)
    {
	ArrayList<String> al = new ArrayList<String>();

        for (String pname : productNames)
        {
            al.add(pname);
            for (String iname : ingredientNames)
            {
                String ingredientLine = createIngredientLine(iname, quantityAsString, unitAsString);
                al.add(ingredientLine);
            }
            al.add("");
        }
        String[] productIngredientArr = al.toArray(new String[al.size()]);
        return productIngredientArr;
    }

    private void printStringArray(String[] sarr)
    {
        for (String line : sarr) 
        {
            System.out.println(line);
        }
    }
      
    private void doProductIngredientsListParse(
       int numberOfIngredients,
       int numberOfProducts,
       Unit unitToUse,
       String unitNameToUse,
       int quantityToUse,
       String quantityStringToUse
    ) 
    throws ParseError, UnknownKeyException
    {
        ProductLine pl = new ProductLine();
        
        String[] ingredientNames = createIngredientsAndAddToProductLine(numberOfIngredients, pl, unitToUse);
        String[] productNames = createProductNames(numberOfProducts);
        String[] productIngredientList = buildProductIngredientsListInputFile(productNames, ingredientNames, quantityStringToUse, unitNameToUse);

        IngredientsListParser ilp = new IngredientsListParser(FILE_NAME, productIngredientList, pl);
        ilp.doParse();

        for (String iname : ingredientNames)
        {
            assertTrue(pl.containsIngredient(iname));
        }

        for (String pname : productNames)
        {
            assertTrue(pl.containsProduct(pname));
            Product product = pl.getProduct(pname);
            Set<String> productIngredientNames = product.getIngredients();
            for (String piName : productIngredientNames ) 
            {
               assertEquals( product.getIngredientQuantity(piName) , quantityToUse );
            }
        }     
    }

    @Test
    public void testCorrectParseinProductIngredientList()
    {
        try {
            doProductIngredientsListParse(50,100,Unit.G,"g", 995, "995");
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitMismatchErrorinProductIngredientList()
    {
        try {
            doProductIngredientsListParse(10,20,Unit.G,"ml", 995, "995");
            fail("Expected Exception");
        }
        catch (ParseError pe) {}
        catch (Exception e) {
            fail("Unexcepected Exception" + e.getMessage());
        }
    }

    @Test
    public void testInvalidUnitParseErrorinProductIngredientList()
    {
        try {
            doProductIngredientsListParse(10,20,Unit.G,"ALOADOFRUBBISH", 995, "995");
            fail("Expected Exception");
        }
        catch (ParseError pe) {}
        catch (Exception e) {
            fail("Unexcepected Exception" + e.getMessage());
        }
    }


    @Test
    public void testBadQuantitySyntaxErrorinProductIngredientList()
    {
        try {
            doProductIngredientsListParse(10,20,Unit.G,"ml", 995, "THISSHOULDBEANUMERICSTRING");
            fail("Expected Exception");
        }
        catch (ParseError pe) {}
        catch (Exception e) {
            fail("Unexcepected Exception" + e.getMessage());
        }
    }

    @Test
    public void testSimpleSyntaxErrorinProductIngredientList()
    {
        ProductLine pl = new ProductLine();
        String[] egNoCommasInIngredients = { "okproductname", "BADINGREDIENTITEM" };
        IngredientsListParser ilp = new IngredientsListParser(FILE_NAME, egNoCommasInIngredients, pl);
        try { 
            ilp.doParse();
            fail("Expected Exception");

        }
        catch (ParseError pe) {}
        catch (Exception e) {
            fail("Unexcepected Exception" + e.getMessage());
        }
    }

    @Test
    public void testUnknownIngredientError()
    {
        ProductLine pl = new ProductLine();
        String[] egNoUnknownIngredient = { "okproductname",  "500,g,this is an unknown ingredient" };
        IngredientsListParser ilp = new IngredientsListParser(FILE_NAME, egNoUnknownIngredient, pl);
        try { 
            ilp.doParse();
            fail("Expected Exception");
        }
        catch (ParseError pe) {}
        catch (Exception e) {
            fail("Unexcepected Exception" + e.getMessage());
        }
    }

    @Test
    public void testDuplicateIngredientErrorinProductIngredientList()
    {
        ProductLine pl = new ProductLine();
        String[] egDuplicateIngredients = { "okproductname", "500,qty,okingredient", "500,qty,okingredient" };

        try { 
            pl.addIngredient(new Ingredient("okingredient", Unit.QTY));
        } catch (DuplicateKeyException dke) {
            fail(dke.getMessage());
        }

        IngredientsListParser ilp = new IngredientsListParser(FILE_NAME, egDuplicateIngredients, pl);
        try { 
            ilp.doParse();
            fail("Expected Exception");
        }
        catch (ParseError pe) {}
        catch (Exception e) {
            fail("Unexcepected Exception " + e.getMessage());
        }
    }

    private String createIngredientStockLevelLine(String name, String unitName, int value)
    {
        return (name + "," + unitName + "," + value);
    }


    static Map<String,Integer> testMapMult = new HashMap<String, Integer>();
    static {
        testMapMult.put("g", 1);
        testMapMult.put("kg", 1000);
        testMapMult.put("ml", 1);
        testMapMult.put("litres", 1000);
        testMapMult.put("qty", 1);
    }
    
    private void doIngredientStockLevelParse(int numberOfIngredients, Unit unit, String unitName)
    throws ParseError
    {
        ProductLine pl = new ProductLine();
        String[] ingredientNames = createIngredients(numberOfIngredients);
        String[] ingredientsStockList = new String[numberOfIngredients];

        int count = 0;
        for (String iname : ingredientNames)
        {
            ingredientsStockList[count] = createIngredientStockLevelLine(iname, unitName, count);
            count++;
        }
     
        IngredientStockLevelParser islp = new IngredientStockLevelParser(FILE_NAME, ingredientsStockList, pl);
        
        islp.doParse();

        Set<String> ingredientsInProductLine = pl.ingredients();
        assertEquals(ingredientsInProductLine.size(), ingredientNames.length);

        for (int value=0; value<numberOfIngredients; value++)
        {
            try {
                Ingredient ingredient = pl.getIngredient(ingredientNames[value]);
                assertEquals (ingredient.unit(), unit);

                int stockLevel = pl.getIngredientStockLevel(ingredientNames[value]);

                assertEquals( value * testMapMult.get(unitName) , stockLevel );
            }
            catch (Exception e) {
                fail("unexpected exception");
            }
        }
     }

    @Test
    public void testCorrectParseOfIngredientStockLevels()
    {
        final int NUM_INGREDIENTS = 200;

        try {
            for (String unitName : Unit.unitStrings())
            {
                doIngredientStockLevelParse(NUM_INGREDIENTS, Unit.unit(unitName), unitName);
            }
        
        }
        catch (Exception e) {
            fail("unexpected error");                
        }
    }

    @Test
    public void testDuplicateInIngredientStockLevels()
    {
        String[] stockLevels = { "ingredient,qty,100", "ingredient2,kg,1000", "ingredient2,g,800" };
        ProductLine pl = new ProductLine();

        try {
            IngredientStockLevelParser islp = new IngredientStockLevelParser(FILE_NAME, stockLevels, pl);
	    islp.doParse();
            fail("Expected Exception");
        }
        catch (ParseError p) {}
        catch (Exception e) {
            fail("unexpected exception");
        }
    }

    @Test
    public void testSyntaxErrorInIngredientStockLevels()
    {
        String[] stockLevels = { "ingredient,qty,100", "ingredient2,kg,1000", ",kg,100" };
        ProductLine pl = new ProductLine();

        try {
            IngredientStockLevelParser islp = new IngredientStockLevelParser(FILE_NAME, stockLevels, pl);
	    islp.doParse();
            fail("Expected Exception");
        }
        catch (ParseError p) {}
        catch (Exception e) {
            fail("unexpected exception");
        }
    }

    private String constructListOfDates(int nDays, String monthAndYear)
    {
        String dates = new String("");
        for (int i=0; i<nDays; i++) 
        {
            dates = new String(dates + "," + (i+1) + "/" + monthAndYear);
        }
        return dates;
    }

    private String constructProductForecast(String productName, int nDays, int quantity)
    {
        String forecast = new String(productName);
        for (int i=0; i < nDays; i++) 
        {
            forecast = new String(forecast + "," + quantity);
        }
        return forecast;
    }

    private void doProductForecastParse(int nProducts)
    throws ParseError
    {
        String[] forecastLines = new String[nProducts + 1];
        final int NDAYS = 20;
        final int FORECAST = 50;

        int count = 0;
        forecastLines[count] = constructListOfDates(NDAYS, "12/92");
        count++;

        String productNames[] = createProductNames(nProducts);
        ProductLine pl = new ProductLine();

        for (String pname : productNames) 
        {
            try {
               pl.addProduct(new Product(pname));
            }
            catch (Exception e) {
                fail("unexpected exception");
            }
            forecastLines[count] = constructProductForecast(pname, NDAYS, FORECAST);
            count++;
        }

        ProductForecastParser pfp = new ProductForecastParser(FILE_NAME, forecastLines, pl);
        pfp.doParse();

        try {
            Set<String> pnames = pl.products();
            int correctForecast = FORECAST;
            for (String pname : pnames)
            {
                Set<Date> dates = pl.getProductForecastDates(pname);
                for (Date aDate : dates)
                {
                    int savedForecast  = pl.getProductForecast(pname,aDate);

                    assertEquals(savedForecast, FORECAST);
                }
            }
        } catch (Exception e) {
            fail("unexpected exception");
        }
    }

    @Test
    public void testCorrectProductForecastParse()
    {
        final int NUM_PRODUCTS = 1000;
        try {
            doProductForecastParse(NUM_PRODUCTS);
        }
        catch (Exception e) {
            fail("parse - unexpected exception" + e.getMessage());
        }
    }

    @Test 
    public void testInvalidDateFormat() 
    {
       String[] invalidInput = { "12/12/33,13/04/44,XXXX" };

       ProductLine pl = new ProductLine();
       ProductForecastParser pfp = new ProductForecastParser(FILE_NAME, invalidInput, pl);

       try {
          pfp.doParse();
          fail("Expected an Exception");  
       }
       catch (ParseError pe) {}
       catch (Exception e) {
           fail("Unexpected Exception - " + e.getMessage());
       }
    }

   @Test
   public void testUnknownProduct()
   {
       String[] invalidInput = { "12/12/33,13/04/44" , "Chicken Nuggets,100" };

       ProductLine pl = new ProductLine();
       ProductForecastParser pfp = new ProductForecastParser(FILE_NAME, invalidInput, pl);

       try {
          pfp.doParse();
          fail("Expected an Exception");  
       }
       catch (ParseError pe) {}
       catch (Exception e) {
           fail("Unexpected Exception - " + e.getMessage());
       }
   }

   @Test
   public void testWrongNumberOfProductForecasts()
   {
       String food = new String("Food");
       String[] invalidInput = { "12/12/33,13/04/44" , food + ",100" };

       ProductLine pl = new ProductLine();
       ProductForecastParser pfp = new ProductForecastParser(FILE_NAME, invalidInput, pl);

       try {
          pl.addProduct(new Product(food));

          pfp.doParse();
          fail("Expected an Exception");  
       }
       catch (ParseError pe) { }
       catch (Exception e) {
           fail("Unexpected Exception - " + e.getMessage());
       }
   }
}

