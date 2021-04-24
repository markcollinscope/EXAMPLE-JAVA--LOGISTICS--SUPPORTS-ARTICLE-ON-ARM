package products;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import utils.*;
import ingredients.*;

public class testProduct { 
    private static final String[] INGREDIENT_NAMES = {
       "Cabbage", "Potato", "Stock" , "Chilli"
    }; 
    private static final String PRODUCT_NAME = "Borsch Soup";
    private static final int QUANTITY = 1000;

    @Test
    public void testIngredientAdditionToProduct()
    {
        try {
            Product p = new Product(PRODUCT_NAME);
            for (String name : INGREDIENT_NAMES) 
            {
                p.addIngredientQuantity(name, QUANTITY);
            }

            Set<String> ingredientNames = p.getIngredients();
            assertEquals(ingredientNames.size(), INGREDIENT_NAMES.length);

            for ( String iname : ingredientNames ) 
            {
                assertTrue(Arrays.asList(INGREDIENT_NAMES).contains(iname));

		
                assertEquals(QUANTITY,p.getIngredientQuantity(iname));
            }
        }
        catch (Exception e) {}
    }

    @Test
    public void testDuplicateIngredientAdditionToProduct()
    {
        try {   
            Product p = new Product(PRODUCT_NAME);
            p.addIngredientQuantity(INGREDIENT_NAMES[0],QUANTITY);
            p.addIngredientQuantity(INGREDIENT_NAMES[0],QUANTITY);
            fail("Expected duplicate exception to be thrown");
        } catch (DuplicateKeyException d) {}
    }

    @Test
    public void testUnknownIngredientQuantityRequest()
    {
        try {   
            Product p = new Product(PRODUCT_NAME);
            p.getIngredientQuantity(INGREDIENT_NAMES[0]);
            fail("Expected unknown ingredient exception to be thrown");

        } catch (UnknownKeyException u) {}
    }
}
