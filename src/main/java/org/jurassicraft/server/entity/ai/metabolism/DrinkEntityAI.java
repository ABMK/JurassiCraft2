package org.jurassicraft.server.entity.ai.metabolism;

import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.jurassicraft.client.animation.Animations;
import org.jurassicraft.server.entity.base.DinosaurEntity;
import org.jurassicraft.server.entity.base.MetabolismContainer;

public class DrinkEntityAI extends EntityAIBase
{
    protected DinosaurEntity dinosaur;

    protected int x, y, z;

    public DrinkEntityAI(DinosaurEntity dinosaur)
    {
        this.dinosaur = dinosaur;
    }

    @Override
    public boolean shouldExecute()
    {
        MetabolismContainer metabolism = dinosaur.getMetabolism();

        if (!dinosaur.isDead && !dinosaur.isCarcass() && dinosaur.ticksExisted % 4 == 0 && dinosaur.worldObj.getGameRules().getBoolean("dinoMetabolism"))
        {
            double water = metabolism.getWater();

            boolean execute = false;

            int maxWater = metabolism.getMaxWater();

            if (water / maxWater * 100 < 25)
            {
                execute = true;
            }
            else
            {
                if (water < maxWater - (maxWater / 8) && dinosaur.getDinosaur().getSleepingSchedule().isWithinEatingTime(dinosaur.getDinosaurTime(), dinosaur.getRNG()))
                {
                    execute = true;
                }
            }

            if (execute)
            {
                int posX = (int) dinosaur.posX;
                int posY = (int) dinosaur.posY;
                int posZ = (int) dinosaur.posZ;

                int closestDist = Integer.MAX_VALUE;
                int closestX = 0;
                int closestY = 0;
                int closestZ = 0;

                boolean found = false;

                World world = dinosaur.worldObj;

                int range = 32;

                for (int x = posX - range; x < posX + range; x++)
                {
                    for (int y = posY - range; y < posY + range; y++)
                    {
                        for (int z = posZ - range; z < posZ + range; z++)
                        {
                            Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();

                            if (block == Blocks.water || block == Blocks.flowing_water)
                            {
                                for (int landX = x - 1; landX < x + 1; landX++)
                                {
                                    for (int landZ = z - 1; landZ < z + 1; landZ++)
                                    {
                                        if (world.getBlockState(new BlockPos(landX, y, landZ)).getBlock().isOpaqueCube())
                                        {
                                            int diffX = posX - landX;
                                            int diffY = posY - y;
                                            int diffZ = posZ - landZ;

                                            int dist = (diffX * diffX) + (diffY * diffY) + (diffZ * diffZ);

                                            if (dist < closestDist)
                                            {
                                                closestDist = dist;
                                                closestX = landX;
                                                closestY = y;
                                                closestZ = landZ;

                                                found = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (found)
                {
                    this.x = closestX;
                    this.y = closestY;
                    this.z = closestZ;
                    dinosaur.getNavigator().tryMoveToXYZ(x, y, z, 1.0);

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void updateTask()
    {
        if ((dinosaur.getDistanceSq(x, y, z) / 16) <= dinosaur.width)
        {
            if (dinosaur.getAnimation() != Animations.DRINKING.get())
            {
                AnimationHandler.INSTANCE.sendAnimationMessage(dinosaur, Animations.DRINKING.get());
            }

            MetabolismContainer metabolism = dinosaur.getMetabolism();
            metabolism.setWater(metabolism.getMaxWater());
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting()
    {
        Block block = dinosaur.worldObj.getBlockState(new BlockPos(x, y, z)).getBlock();

        return dinosaur != null && !this.dinosaur.getNavigator().noPath() && (block == Blocks.water || block == Blocks.flowing_water);
    }
}
