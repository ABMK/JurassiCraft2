package org.jurassicraft.server.dinosaur;

import org.jurassicraft.server.entity.GiganotosaurusEntity;
import org.jurassicraft.server.entity.base.EnumDiet;
import org.jurassicraft.server.period.EnumTimePeriod;

public class GiganotosaurusDinosaur extends Dinosaur
{
    public GiganotosaurusDinosaur()
    {
        super();

        this.setName("Giganotosaurus");
        this.setDinosaurClass(GiganotosaurusEntity.class);
        this.setTimePeriod(EnumTimePeriod.CRETACEOUS);
        this.setEggColorMale(0x4F3F33, 0x4F3F33);
        this.setEggColorFemale(0x756E54, 0x4B474A);
        this.setHealth(20, 80);
        this.setSpeed(0.52, 0.40);
        this.setStrength(5, 35);
        this.setEyeHeight(0.6F, 4.8F);
        this.setSizeX(0.5F, 4.0F);
        this.setSizeY(0.8F, 5.8F);
        this.setStorage(54);
        this.setDiet(EnumDiet.CARNIVORE);
        this.setBones("skull", "tooth");
        this.setHeadCubeName("Head");
        this.setScale(2.37F, 0.3F);
        this.disableRegistry();
    }
}
