package org.jurassicraft.server.entity.ai.metabolism;

import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jurassicraft.client.animation.Animations;
import org.jurassicraft.server.entity.base.DinosaurEntity;
import org.jurassicraft.server.entity.base.MetabolismContainer;
import org.jurassicraft.server.food.FoodHandler;

import java.util.List;

public class EatFoodItemEntityAI extends EntityAIBase
{
    protected DinosaurEntity dinosaur;

    protected EntityItem item;

    public EatFoodItemEntityAI(DinosaurEntity dinosaur)
    {
        this.dinosaur = dinosaur;
    }

    @Override
    public boolean shouldExecute()
    {
        MetabolismContainer metabolism = dinosaur.getMetabolism();

        if (!dinosaur.isDead && !dinosaur.isCarcass() && dinosaur.ticksExisted % 4 == 0 && dinosaur.worldObj.getGameRules().getBoolean("dinoMetabolism"))
        {
            //LOGGER.info("EatFoodItemEntityAI executing: " + dinosaur.getName());
            double food = metabolism.getFood();

            boolean execute = false;

            int maxFood = metabolism.getMaxFood();

            if (food / maxFood * 100 < 25)
            {
                execute = true;
            }
            else
            {
                if (food < maxFood - (maxFood / 8) && dinosaur.getDinosaur().getSleepingSchedule().isWithinEatingTime(dinosaur.getDinosaurTime(), dinosaur.getRNG()))
                {
                    execute = true;
                }
            }

            if (execute)
            {
                double posX = dinosaur.posX;
                double posY = dinosaur.posY;
                double posZ = dinosaur.posZ;

                double closestDist = Integer.MAX_VALUE;
                EntityItem closest = null;

                boolean found = false;

                World world = dinosaur.worldObj;

                List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.fromBounds(posX - 16, posY - 16, posZ - 16, posX + 16, posY + 16, posZ + 16));

                for (EntityItem e : items)
                {
                    ItemStack stack = e.getEntityItem();

                    if (stack != null)
                    {
                        Item item = stack.getItem();

                        if (FoodHandler.INSTANCE.canDietEat(dinosaur.getDinosaur().getDiet(), item))
                        {
                            double diffX = posX - e.posX;
                            double diffY = posY - e.posY;
                            double diffZ = posZ - e.posZ;

                            double dist = (diffX * diffX) + (diffY * diffY) + (diffZ * diffZ);

                            if (dist < closestDist)
                            {
                                closestDist = dist;
                                closest = e;

                                found = true;
                            }
                        }
                    }
                }

                if (found)
                {
                    dinosaur.getNavigator().tryMoveToEntityLiving(closest, 1.0);
                    this.item = closest;

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void updateTask()
    {
        if (dinosaur.getEntityBoundingBox().intersectsWith(item.getEntityBoundingBox().expand(0.5D, 0.5D, 0.5D)))
        {
            AnimationHandler.INSTANCE.sendAnimationMessage(dinosaur, Animations.EATING.get());

            if (dinosaur.worldObj.getGameRules().getBoolean("mobGriefing"))
            {
                if (item.getEntityItem().stackSize > 1)
                {
                    item.getEntityItem().stackSize--;
                }
                else
                {
                    item.setDead();
                }
            }

            dinosaur.getMetabolism().increaseFood(2000);
            dinosaur.heal(4.0F);
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting()
    {
        return dinosaur != null && !this.dinosaur.getNavigator().noPath() && item != null && !item.isDead;
    }

    private static final Logger LOGGER = LogManager.getLogger();
}
