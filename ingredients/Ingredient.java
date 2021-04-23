package ingredients;

public class Ingredient 
{
    private String name;
    private Unit unit;
    private int shelfLife;

    public static final int UNKNOWN_SHELF_LIFE = -1;
    
    public Ingredient(String name, Unit unit)
    {
       this(name, unit, UNKNOWN_SHELF_LIFE);
    }

    public Ingredient(String name, Unit unit, int shelfLife)
    {  
       this.name = name;
       this.unit = unit;
       this.shelfLife = shelfLife;
    }

    public String name()
    {
        return this.name;
    }

    public Unit unit() 
    {
        return this.unit;
    }

    public int shelfLife() 
    {
        return this.shelfLife;
    }
}
