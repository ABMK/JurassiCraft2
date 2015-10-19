package org.jurassicraft.common.vehicles.helicopter.modules;

import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.jurassicraft.common.vehicles.helicopter.EntityHelicopterBase;

import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Entity representing a seat inside the helicopter. Should NOT be spawned inside the world, the {@link EntityHelicopterBase Helicopter Entity} handles that for you.
 */
public class EntityHelicopterSeat extends Entity implements IEntityAdditionalSpawnData
{
    private boolean sitedRider;
    private UUID parentID;
    private float dist;
    private int index;
    public EntityHelicopterBase parent;

    public EntityHelicopterSeat(World worldIn)
    {
        super(worldIn);
        setEntityBoundingBox(AxisAlignedBB.fromBounds(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
        noClip = true;
        parentID = UUID.randomUUID();
        sitedRider = true;
    }

    public EntityHelicopterSeat(float dist, int index, EntityHelicopterBase parent, boolean sitedRider)
    {
        super(parent.getEntityWorld());
        setEntityBoundingBox(AxisAlignedBB.fromBounds(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
        this.dist = dist;
        this.index = index;
        this.parent = checkNotNull(parent, "parent");
        parentID = parent.getHeliID();
        noClip = true;
        resetPos();
        this.sitedRider = sitedRider;
    }

    @Override
    protected void entityInit()
    {
        width = 0f;
        height = 0f;
    }

    @Override
    public void onEntityUpdate()
    {
        motionX = 0f;
        motionY = 0f;
        motionZ = 0f;
        super.onEntityUpdate();
        if (parent == null) // we are in this state right after reloading a map
        {
            parent = getParentFromID(worldObj, parentID);
        }
        if (parent != null)
        {
            float angle = parent.rotationYaw;

            resetPos();
            if (parent.isDead)
            {
                System.out.println("KILLED");
                kill();
            }
        }
        else
        {
            System.out.println("no parent :c " + parentID);
        }
    }

    private void resetPos()
    {
        float nx = -MathHelper.sin(parent.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(parent.rotationPitch / 180.0F * (float) Math.PI) * dist;
        float nz = MathHelper.cos(parent.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(parent.rotationPitch / 180.0F * (float) Math.PI) * dist;
        float ny = -MathHelper.sin((parent.rotationPitch)) / 180.0F * (float) Math.PI * dist;

        this.posX = parent.posX + nx;
        this.posY = parent.posY + ny + 0.4f;
        this.posZ = parent.posZ + nz;
        System.out.println(">> new pos: "+posX+", "+posY+", "+posZ);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound)
    {
        dist = tagCompound.getFloat("dist");
        index = tagCompound.getInteger("index");
        sitedRider = tagCompound.getBoolean("sitedRider");
        parentID = UUID.fromString(tagCompound.getString("heliID"));
    }

    public static EntityHelicopterBase getParentFromID(World worldObj, final UUID id)
    {
        List<EntityHelicopterBase> list = worldObj.getEntities(EntityHelicopterBase.class, new Predicate<Entity>()
        {
            @Override
            public boolean apply(Entity input)
            {
                if(input instanceof EntityHelicopterBase)
                {
                    EntityHelicopterBase helicopterBase = (EntityHelicopterBase)input;
                    return helicopterBase.getHeliID().equals(id);
                }
                return false;
            }
        });
        if (list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setFloat("dist", dist);
        tagCompound.setInteger("index", index);
        tagCompound.setBoolean("sitedRider", sitedRider);
        tagCompound.setString("heliID", parentID.toString());
    }

    public EntityHelicopterBase getParent()
    {
        return parent;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public double getMountedYOffset()
    {
        return 0f;
    }

    public void setParentID(UUID parentID)
    {
        this.parentID = parentID;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, parentID.toString());
        buffer.writeFloat(dist);
        buffer.writeBoolean(sitedRider);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        parentID = UUID.fromString(ByteBufUtils.readUTF8String(additionalData));
        dist = additionalData.readFloat();
        sitedRider = additionalData.readBoolean();
    }

    @Override
    public boolean shouldRiderSit()
    {
        return sitedRider;
    }
}
