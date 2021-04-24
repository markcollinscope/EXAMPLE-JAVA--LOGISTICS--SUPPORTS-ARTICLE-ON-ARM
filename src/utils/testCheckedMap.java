package utils;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class testCheckedMap { 
    private static final String[] NAMES = {
       "Cabbage", "Potato", "Stock" , "Chilli"
    }; 
    
    @Test
    public void testAddition()
    {
        try {
            CheckedMap<String,String> csm = new CheckedMap<String,String>();
            for (int i=0; i<NAMES.length; i++) 
            {
                csm.put(NAMES[i],NAMES[i]);
            }

            Set<String> keys = csm.keySet();
            assertEquals(keys.size(), NAMES.length);

            for ( String aKey : keys ) 
            {
                assertTrue(Arrays.asList(NAMES).contains(aKey));
                assertTrue(csm.containsKey(aKey));

		String value = csm.get(aKey);
                assertEquals(aKey,value);
            }
        }
        catch (Exception e) {
           fail("unexpected exception");
        }
    }

    @Test
    public void testDuplicateAddition()
    {
        String key = "key", value = "value";

        try {   
            CheckedMap<String,String> csm = new CheckedMap<String,String>();
            csm.put(key,value);
            csm.put(key,value);
            fail("Expected duplicate exception to be thrown");

        } catch (DuplicateKeyException dke) {}
    }

    @Test
    public void testUnknownKeyRequest()
    {
        try {   
            CheckedMap<String,String> csm= new CheckedMap<String,String>();
            csm.get(NAMES[0]);
            fail("Expected unknown ingredient exception to be thrown");

        } catch (UnknownKeyException uke) {}
    }
}




