package org.jurassicraft.common.dinosaur;

import org.jurassicraft.common.entity.EntityMoganopterus;
import org.jurassicraft.common.entity.base.EntityDinosaur;
import org.jurassicraft.common.period.EnumTimePeriod;

public class DinosaurMoganopterus extends Dinosaur
{
    @Override
    public String getName()
    {
        return "Moganopterus";
    }

    @Override
    public Class<? extends EntityDinosaur> getDinosaurClass()
    {
        return EntityMoganopterus.class;
    }

    @Override
    public EnumTimePeriod getPeriod()
    {
        return EnumTimePeriod.CRETACEOUS;
    }

    @Override
    public int getEggPrimaryColorMale()
    {
        return 0xE6E2D8;
    }

    @Override
    public int getEggSecondaryColorMale()
    {
        return 0xD67F5C;
    }

    @Override
    public int getEggPrimaryColorFemale()
    {
        return 0xE0DED3;
    }

    @Override
    public int getEggSecondaryColorFemale()
    {
        return 0xD37B58;
    }

    @Override
    public double getBabyHealth()
    {
        return 16;
    }

    @Override
    public double getAdultHealth()
    {
        return 55;
    }

    @Override
    public double getBabySpeed()
    {
        return 0.46;
    }

    @Override
    public double getAttackSpeed()
    {
        return 0.50;
    }

    @Override
    public double getAdultSpeed()
    {
        return 0.40;
    }

    @Override
    public double getBabyStrength()
    {
        return 6;
    }

    @Override
    public double getAdultStrength()
    {
        return 36;
    }

    @Override
    public double getBabyKnockback()
    {
        return 0.3;
    }

    @Override
    public double getAdultKnockback()
    {
        return 0.6;
    }

    @Override
    public int getMaximumAge()
    {
        return fromDays(40);
    }

    @Override
    public float getBabyEyeHeight()
    {
        return 0.225F;
    }

    @Override
    public float getAdultEyeHeight()
    {
        return 1.3F;
    }

    @Override
    public float getBabySizeX()
    {
        return 0.3F;
    }

    @Override
    public float getBabySizeY()
    {
        return 0.3F;
    }

    @Override
    public float getAdultSizeX()
    {
        return 1.0F;
    }

    @Override
    public float getAdultSizeY()
    {
        return 1.3F;
    }

    @Override
    public int getStorage()
    {
        return 27;
    }
}
