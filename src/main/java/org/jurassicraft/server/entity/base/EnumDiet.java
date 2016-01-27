package org.jurassicraft.server.entity.base;

public enum EnumDiet
{
    HERBIVORE(false, true, false, 0x007F0E), CARNIVORE(true, false, true, 0xB70000), OMNIVORE(true, true, true, 0xB77F0E), PISCIVORE(false, false, true, 0x437EA8);

    private boolean eatsMeat;
    private boolean eatsPlants;
    private boolean eatsFish;

    private int color;

    EnumDiet(boolean eatsMeat, boolean eatsPlants, boolean eatsFish, int color)
    {
        this.eatsMeat = eatsMeat;
        this.eatsPlants = eatsPlants;
        this.eatsFish = eatsFish;
        this.color = color;
    }

    public boolean doesEatMeat()
    {
        return eatsMeat;
    }

    public boolean doesEatPlants()
    {
        return eatsPlants;
    }

    public boolean doesEatFish()
    {
        return eatsFish;
    }

    public int getColor()
    {
        return color;
    }
}
