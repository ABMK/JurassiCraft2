package org.jurassicraft.server.plant;

import java.util.ArrayList;
import java.util.List;

public enum PlantHandler
{
    INSTANCE;

    private List<Plant> plants = new ArrayList<>();

    public Plant ajuginucula_smithii;
    public Plant small_royal_fern;
    public Plant calamites;
    public Plant small_chain_fern;
    public Plant small_cycad;
    public Plant ginkgo;
    public Plant bennettitalean_cycadeoidea;
    public Plant cry_pansy;
    public Plant scaly_tree_fern;
    public Plant cycad_zamites;
    public Plant dicksonia;
    public Plant wild_onion;

    public void init()
    {
        ajuginucula_smithii = new AjuginuculaSmithiiPlant();
        small_royal_fern = new SmallRoyalFernPlant();
        calamites = new CalamitesPlant();
        small_chain_fern = new SmallChainFernPlant();
        small_cycad = new SmallCycadPlant();
        ginkgo = new GinkgoPlant();
        bennettitalean_cycadeoidea = new BennettitaleanCycadeoideaPlant();
        cry_pansy = new CryPansyPlant();
        scaly_tree_fern = new ScalyTreeFernPlant();
        cycad_zamites = new ZamitesPlant();
        dicksonia = new DicksoniaPlant();
        wild_onion = new WildOnionPlant();

        registerPlant(ajuginucula_smithii);
        registerPlant(small_royal_fern);
        registerPlant(calamites);
        registerPlant(small_chain_fern);
        registerPlant(small_cycad);
        registerPlant(ginkgo);
        registerPlant(bennettitalean_cycadeoidea);
        registerPlant(cry_pansy);
        registerPlant(scaly_tree_fern);
        registerPlant(cycad_zamites);
        registerPlant(dicksonia);
        registerPlant(wild_onion);
    }

    public Plant getPlantById(int id)
    {
        if (id >= plants.size() || id < 0)
        {
            return null;
        }

        return plants.get(id);
    }

    public int getPlantId(Plant plant)
    {
        return plants.indexOf(plant);
    }

    public List<Plant> getPlants()
    {
        return plants;
    }

    public void registerPlant(Plant plant)
    {
        if (!plants.contains(plant))
        {
            plants.add(plant);
        }
    }
}
