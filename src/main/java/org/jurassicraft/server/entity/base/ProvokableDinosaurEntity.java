package org.jurassicraft.server.entity.base;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class ProvokableDinosaurEntity extends DinosaurEntity
{
    public ProvokableDinosaurEntity(World world)
    {
        super(world);
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, true));
    }

    /**
     * Sets the active target the Task system uses for tracking
     */
    @Override
    public void setAttackTarget(EntityLivingBase entity)
    {
        super.setAttackTarget(entity);

        if (entity != null)
        {
            this.setAngry(true);
        }
        else
        {
            this.setAngry(false);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("Angry", this.isAngry());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);
        this.setAngry(tagCompund.getBoolean("Angry"));
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons use this to react to sunlight and start to burn.
     */
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote && this.getAttackTarget() == null && this.isAngry())
        {
            this.setAngry(false);
        }
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte) 0);
    }

    /**
     * Determines whether this wolf is angry or not.
     */
    public boolean isAngry()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
    }

    /**
     * Sets whether this wolf is angry or not.
     */
    public void setAngry(boolean angry)
    {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);

        if (angry)
        {
            this.dataWatcher.updateObject(16, (byte) (b0 | 2));
        }
        else
        {
            this.dataWatcher.updateObject(16, (byte) (b0 & -3));
        }
    }
}
