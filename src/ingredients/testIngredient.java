package ingredients;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;


public class testIngredient { 
    private static final String INGREDIENT_NAME = "Potato"; 
    private static final Unit A_UNIT = Unit.G;
    private static final int SHELF_LIFE = 5;

    @Test
    public void testConstructors()
    {
        Ingredient anIngredient = new Ingredient(INGREDIENT_NAME,A_UNIT,SHELF_LIFE);
        assertEquals(INGREDIENT_NAME,anIngredient.name());
        assertEquals(A_UNIT,anIngredient.unit());
        assertEquals(SHELF_LIFE,anIngredient.shelfLife());

        Ingredient anotherIngredient = new Ingredient(INGREDIENT_NAME,A_UNIT);
        assertEquals(INGREDIENT_NAME,anotherIngredient.name());
        assertEquals(A_UNIT,anotherIngredient.unit());
        assertEquals(Ingredient.UNKNOWN_SHELF_LIFE, anotherIngredient.shelfLife());
    }

    private static Map<String,Unit> testMapUnits = new HashMap<String,Unit>();
    private static Map<String,Integer> testMapMult = new HashMap<String, Integer>();
    static {
        testMapUnits.put("g", Unit.G);
        testMapMult.put("g", 1);
        testMapUnits.put("kg", Unit.G);
        testMapMult.put("kg", 1000);
        testMapUnits.put("ml", Unit.ML);
        testMapMult.put("ml", 1);
        testMapUnits.put("litres", Unit.ML);
        testMapMult.put("litres", 1000);
        testMapUnits.put("qty", Unit.QTY);
        testMapMult.put("qty", 1);
    }

    @Test
    public void testUnits()
    {
        for (String uName : Unit.unitStrings()) 
        {
            try {
                assertEquals(Unit.unit(uName), testMapUnits.get(uName));
                assertEquals((int)Unit.multiplicationFactor(uName), (int)testMapMult.get(uName));
            }
            catch (Exception e) {
               fail(e.getMessage());
            }
        }
    }
 }
