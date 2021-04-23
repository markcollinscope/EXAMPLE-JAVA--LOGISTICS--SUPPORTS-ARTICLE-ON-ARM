package productLines;

import ingredients.*;
import products.*;
import utils.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.text.DecimalFormat;
import static org.junit.Assert.*;

public class ProductLine {

    private CheckedMap<String,Ingredient> availableIngredientsList =
        new CheckedMap<String,Ingredient>();

    private CheckedMap<String,Product> availableProductsList =
       new CheckedMap<String,Product>(); 

    private CheckedMap<String,CheckedMap<Date,Integer>> productSalesForecast = 
       new CheckedMap<String,CheckedMap<Date,Integer>>();

    private CheckedMap<String, Integer> ingredientStockLevel =
       new CheckedMap<String, Integer>();

    private Set<Date> allDates = new HashSet<Date>();

    public void addProduct(Product p)
    throws DuplicateKeyException
    {
        availableProductsList.put(p.name(), p);

        CheckedMap<Date,Integer> cm = new CheckedMap<Date,Integer>();
        productSalesForecast.put(p.name(), cm);
    }

    public Set<String> products()
    {
        return availableProductsList.keySet();
    }

    public Product getProduct(String productName)
    throws UnknownKeyException
    {
        return availableProductsList.get(productName);
    }

    public Boolean containsProduct(String productName)
    {
        return availableProductsList.containsKey(productName);
    }

    public Boolean containsIngredient(String ingredientName)
    {
        return availableIngredientsList.containsKey(ingredientName);
    }

    public void addIngredient(Ingredient ingredient)
    throws DuplicateKeyException
    {
        availableIngredientsList.put(ingredient.name(), ingredient);
    }

    public Set<String> ingredients()
    {
        return availableIngredientsList.keySet();
    }

    public Ingredient getIngredient(String ingredientName)
    throws UnknownKeyException
    {
        return availableIngredientsList.get(ingredientName);     
    }
    
    public void addProductForecast(String productName, Date date, int value)
    throws DuplicateKeyException, UnknownKeyException, UninitialisedProductException
    {
        if (!availableProductsList.containsKey(productName)) 
        {
            throw new UninitialisedProductException("name - " + productName);
        }
        allDates.add(date);        
        productSalesForecast.get(productName).put(date,value);
    }

    public int getProductForecast(String productName, Date date)
    throws UnknownKeyException
    {
        return (productSalesForecast.get(productName)).get(date);
    }

    public Set<Date> getProductForecastDates(String productName) 
    throws UnknownKeyException
    {
        return (productSalesForecast.get(productName).keySet());
    }

    public Date[] getOrderedProductForecastDates(String productName)
    throws UnknownKeyException
    {
       Date[] dates = getProductForecastDates(productName).toArray(new Date[0]);
       Arrays.sort(dates);
       return dates;
    }

    public Date[] getAllDates()
    {
        Date[] dates = allDates.toArray(new Date[0]);
        Arrays.sort(dates);
        return dates;
    }

    public void addIngredientStockLevel(String ingredientName, int level)
    throws DuplicateKeyException, UnknownKeyException, UninitialisedIngredientException
    {
        if (!availableIngredientsList.containsKey(ingredientName))
        {
           throw new UninitialisedIngredientException("name - " + ingredientName);
        }
        ingredientStockLevel.put(ingredientName, level);        
    }

    public int getIngredientStockLevel(String ingredientName)
    throws UnknownKeyException
    {
        return ingredientStockLevel.get(ingredientName);
    }

    public int calculateIngredientRequirementByDate(String ingredientName, Date date)
    {
        Set<String> allProductNames = this.products();
        int requiredAmount = 0;

        for (String aProductName : allProductNames)
        {
            try {            
                Product p = this.getProduct(aProductName);
             
                Set<String> ingredientNames = p.getIngredients();
                Set<Date> forecastDates = this.getProductForecastDates(aProductName);

                if (ingredientNames.contains(ingredientName) && forecastDates.contains(date))
                {
                   int numberOfRequiredProducts = this.getProductForecast(aProductName, date);
                   int ingredientQuantityPerProduct = p.getIngredientQuantity(ingredientName);

                   requiredAmount += (numberOfRequiredProducts * ingredientQuantityPerProduct);
                }
            }
            catch (Exception e) {
                  ProgrammingError.abort(e.getMessage());
            }
        }
        return requiredAmount;
    }

    public double calculateNumberOfDaysIngredientsWillCover(String ingredientName)
    throws UnknownKeyException
    {
        int stillInStock = this.getIngredientStockLevel(ingredientName);
       
        int nDays = 0;
        double lastDayFractionCovered = 0;
        for (Date aDate : this.getAllDates())
        {
            int nIngredientsRequiredForDate = this.calculateIngredientRequirementByDate(ingredientName, aDate);
            if (stillInStock >= nIngredientsRequiredForDate)
            {
                nDays++;
                stillInStock -= nIngredientsRequiredForDate;
            }
            else 
            {
                lastDayFractionCovered = ((double)stillInStock / (double)nIngredientsRequiredForDate);
                break;
            }
        }
        Double resultUnrounded = new Double((double)nDays + (lastDayFractionCovered));

        DecimalFormat toOneDecimalPlace = new DecimalFormat("#.#");
        return (double) (Double.valueOf(toOneDecimalPlace.format(resultUnrounded)));
    }
}
