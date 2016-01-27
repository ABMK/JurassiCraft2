package org.jurassicraft.server.world.islanublar;

import net.ilexiconn.llibrary.common.world.gen.WorldHeightmapGenerator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class IslaNublarGeneration extends WorldHeightmapGenerator
{
    @Override
    public String getBiomeMapLocation()
    {
        return null;
    }

    @Override
    public String getHeightmapLocation()
    {
        return "/assets/jurassicraft/textures/heightmap/isla_nublar.png";
    }

    @Override
    public double getWorldScale()
    {
        return 1.0;
    }

    @Override
    public int adjustHeight(int x, int z, int originalHeight)
    {
        return originalHeight;
    }

    @Override
    public IBlockState getStoneBlock()
    {
        return Blocks.stone.getDefaultState();
    }

    @Override
    public BiomeGenBase getDefaultBiome()
    {
        return BiomeGenBase.plains;
    }

    @Override
    public String getName()
    {
        return "Isla Nublar";
    }

    @Override
    public int getColourForBiome(BiomeGenBase biomeGenBase)
    {
        return 0;
    }

    @Override
    public boolean hasOcean()
    {
        return true;
    }

    @Override
    public IBlockState getOceanLiquid()
    {
        return Blocks.water.getDefaultState();
    }

    @Override
    public int getOceanHeight(int i, int i1)
    {
        return 62;
    }

    @Override
    public int getOutOfBoundsHeight(int i, int i1)
    {
        return 0;
    }

    @Override
    public boolean loadHeightmapIntoArray()
    {
        return false;
    }

    @Override
    public boolean loadBiomemapIntoArray()
    {
        return false;
    }
}
