package ingredients;

import utils.*;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

class UnitMultiplicationFactor
{
    public Unit unit;
    public int multiplicationFactor;

    UnitMultiplicationFactor(Unit unit, int mult)
    {
        this.unit = unit;
        this.multiplicationFactor = mult;
    }
}

public enum Unit
{
   G, ML, QTY, UNDEFINED;

   private static final CheckedMap<String,UnitMultiplicationFactor> STRING_TO_UNIT_MAP = new CheckedMap<String,UnitMultiplicationFactor>() {
       {
           try {
               put("g", new UnitMultiplicationFactor(G,1));
               put("kg", new UnitMultiplicationFactor(G,1000));
               put("ml", new UnitMultiplicationFactor(ML,1));
               put("litres", new UnitMultiplicationFactor(ML,1000));
               put("qty", new UnitMultiplicationFactor(QTY,1));
          } catch (Exception e) {
              ProgrammingError.abort("initialising STRING_TO_UNIT_MAP");
          }
       }
  };

   private static final CheckedMap<Unit,String> UNIT_TO_STRING_MAP = new CheckedMap<Unit,String>() {
       {
           try {
               put(G, "g");
               put(ML, "ml");
               put(QTY, "qty");
           }
           catch (Exception e) {
               ProgrammingError.abort("initialising UNIT_TO_STRING_MAP");
           }
       }
   };

   public static String unitName(Unit u)
   throws UnknownKeyException
   {
       return UNIT_TO_STRING_MAP.get(u);
   }
   
   public static Unit unit(String uname)
   throws UnknownKeyException
   {
      return STRING_TO_UNIT_MAP.get(uname).unit;
   }

   public static int multiplicationFactor(String uname)
   throws UnknownKeyException
   {
      return STRING_TO_UNIT_MAP.get(uname).multiplicationFactor;
   }

   public static Set<String> unitStrings()
   {
      return STRING_TO_UNIT_MAP.keySet();
   }
}
