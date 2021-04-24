package products;

import utils.*;

import java.util.Set;
import java.util.HashMap;

public class Product {
    private String name;

    private CheckedMap<String,Integer> masterIngredientList = 
        new CheckedMap<String,Integer>();

    public Product(String name)
    {
        this.name = name;
    }

    public String name()
    {
         return this.name;
    }

    public void addIngredientQuantity(String ingredient, int quantity)
    throws DuplicateKeyException
    { 
        masterIngredientList.put(ingredient, quantity);
    }

    public Set<String> getIngredients() 
    {
        return masterIngredientList.keySet();
    }

    public int getIngredientQuantity(String ingredient)
    throws UnknownKeyException
    { 
        return masterIngredientList.get(ingredient);
    }

}
