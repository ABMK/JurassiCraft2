package org.jurassicraft.server.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.container.DNASynthesizerContainer;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.base.DinosaurEntity;
import org.jurassicraft.server.entity.base.JCEntityRegistry;
import org.jurassicraft.server.entity.item.CageSmallEntity;
import org.jurassicraft.server.food.JCFoodNutrients;
import org.jurassicraft.server.item.ItemHandler;

import java.util.List;
import java.util.Random;

public class CultivatorTile extends TileEntityLockable implements ITickable, ISidedInventory
{
    private static final int[] slotsTop = new int[] { 0, 1 }; // input (embryo + nutrients)
    private static final int[] slotsBottom = new int[] { 4 }; // output
    private static final int[] slotsSides = new int[] { 2, 3 }; // buckets

    private int waterLevel;

    private int lipids;
    private int proximates;
    private int minerals;
    private int vitamins;

    private int maxNutrients = 3000;

    /**
     * The ItemStacks that hold the items currently being used in the cultivator
     */
    private ItemStack[] slots = new ItemStack[5];

    private int cultivateTime;
    private int totalCultivateTime;

    private String customName;

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return this.slots.length;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int index)
    {
        return this.slots[index];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
     */
    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.slots[index] != null)
        {
            ItemStack itemstack;

            if (this.slots[index].stackSize <= count)
            {
                itemstack = this.slots[index];
                this.slots[index] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.slots[index].splitStack(count);

                if (this.slots[index].stackSize == 0)
                {
                    this.slots[index] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem - like when you close a workbench GUI.
     */
    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        if (this.slots[index] != null)
        {
            ItemStack itemstack = this.slots[index];
            this.slots[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        boolean flag = stack != null && stack.isItemEqual(this.slots[index]) && ItemStack.areItemStackTagsEqual(stack, this.slots[index]);
        this.slots[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        if (index == 0 && !flag)
        {
            this.totalCultivateTime = this.getCultivateTime(stack);
            this.cultivateTime = 0;
            this.markDirty();
        }
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    @Override
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.cultivator";
    }

    /**
     * Returns true if this thing is named
     */
    @Override
    public boolean hasCustomName()
    {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomInventoryName(String customName)
    {
        this.customName = customName;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        NBTTagList itemList = compound.getTagList("Items", 10);
        this.slots = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < itemList.tagCount(); ++i)
        {
            NBTTagCompound item = itemList.getCompoundTagAt(i);

            byte slot = item.getByte("Slot");

            if (slot >= 0 && slot < this.slots.length)
            {
                this.slots[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }

        this.cultivateTime = compound.getShort("CultivateTime");
        this.totalCultivateTime = compound.getShort("CultivateTimeTotal");

        this.waterLevel = compound.getShort("WaterLevel");
        this.lipids = compound.getInteger("Lipids");
        this.minerals = compound.getInteger("Minerals");
        this.vitamins = compound.getInteger("Vitamins");
        this.proximates = compound.getInteger("Proximates");

        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setShort("CultivateTime", (short) this.cultivateTime);
        compound.setShort("CultivateTimeTotal", (short) this.totalCultivateTime);
        compound.setShort("WaterLevel", (short) this.waterLevel);
        compound.setInteger("Lipids", lipids);
        compound.setInteger("Minerals", minerals);
        compound.setInteger("Vitamins", vitamins);
        compound.setInteger("Proximates", proximates);
        NBTTagList itemList = new NBTTagList();

        for (int slot = 0; slot < this.slots.length; ++slot)
        {
            if (this.slots[slot] != null)
            {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) slot);

                this.slots[slot].writeToNBT(itemTag);
                itemList.appendTag(itemTag);
            }
        }

        compound.setTag("Items", itemList);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean isCultivating()
    {
        return this.cultivateTime > 0;
    }

    @SideOnly(Side.CLIENT)
    public static boolean isCultivating(IInventory inventory)
    {
        return inventory.getField(0) > 0;
    }

    /**
     * Updates the JList with a new model.
     */
    @Override
    public void update()
    {
        boolean flag = this.isCultivating();
        boolean sync = false;

        if (!this.worldObj.isRemote)
        {
            if (!this.isCultivating() && (this.slots[0] == null))
            {
                if (this.cultivateTime > 0)
                {
                    this.cultivateTime = MathHelper.clamp_int(this.cultivateTime - 2, 0, this.totalCultivateTime);
                }
            }
            else
            {
                if (this.canCultivate())
                {
                    ++this.cultivateTime;

                    if (this.cultivateTime == this.totalCultivateTime)
                    {
                        this.cultivateTime = 0;
                        this.totalCultivateTime = this.getCultivateTime(this.slots[0]);
                        this.cultivate();
                        sync = true;
                    }
                }
                else
                {
                    this.cultivateTime = 0;
                    sync = true;
                }
            }

            if (waterLevel < 3 && slots[2] != null && slots[2].getItem() == Items.water_bucket)
            {
                if (slots[3] == null || slots[3].stackSize < 16)
                {
                    slots[2].stackSize--;

                    if (slots[2].stackSize <= 0)
                    {
                        slots[2] = null;
                    }

                    waterLevel++;

                    if (slots[3] == null)
                    {
                        slots[3] = new ItemStack(Items.bucket);
                    }
                    else if (slots[3].getItem() == Items.bucket)
                    {
                        slots[3].stackSize++;
                    }

                    sync = true;
                }
            }

            if (slots[1] != null && JCFoodNutrients.FOODLIST.containsKey(slots[1].getItem()))
            {
                if ((proximates < maxNutrients) || (minerals < maxNutrients) || (vitamins < maxNutrients) || (lipids < maxNutrients))
                {
                    consumeNutrients();
                    sync = true;
                }
            }

            if (flag != this.isCultivating())
            {
                sync = true;
            }
        }
        else
        {
            if (this.canCultivate())
            {
                ++this.cultivateTime;
            }
        }

        if (sync)
        {
            this.markDirty();
        }
    }

    private void consumeNutrients()
    {
        JCFoodNutrients nutrients = JCFoodNutrients.values()[JCFoodNutrients.FOODLIST.get(slots[1].getItem())];

        if (this.slots[1].getItem() instanceof ItemBucketMilk)
        {
            this.slots[1] = null;
            this.slots[1] = new ItemStack(Items.bucket);
        }
        else
        {
            this.slots[1].stackSize--;

            if (this.slots[1].stackSize <= 0)
            {
                this.slots[1] = null;
            }
        }

        Random random = new Random();

        if (proximates < maxNutrients)
        {
            proximates = (short) (proximates + (800 + random.nextInt(201)) * nutrients.getProximate());

            if (proximates > maxNutrients)
            {
                proximates = (short) maxNutrients;
            }
        }

        if (minerals < maxNutrients)
        {
            minerals = (short) (minerals + (900 + random.nextInt(101)) * nutrients.getMinerals());

            if (minerals > maxNutrients)
            {
                minerals = (short) maxNutrients;
            }
        }

        if (vitamins < maxNutrients)
        {
            vitamins = (short) (vitamins + (900 + random.nextInt(101)) * nutrients.getVitamins());

            if (vitamins > maxNutrients)
            {
                vitamins = (short) maxNutrients;
            }
        }

        if (lipids < maxNutrients)
        {
            lipids = (short) (lipids + (980 + random.nextInt(101)) * nutrients.getLipids());

            if (lipids > maxNutrients)
            {
                lipids = (short) maxNutrients;
            }
        }
    }

    public int getCultivateTime(ItemStack stack)
    {
        return 2000;
    }

    /**
     * Returns true if the cultivator can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canCultivate()
    {
        if (slots[0] != null && slots[0].getItem() == ItemHandler.INSTANCE.syringe && waterLevel == 3)
        {
            Dinosaur dino = JCEntityRegistry.getDinosaurById(slots[0].getItemDamage());

            if (dino != null)
            {
                if (dino.isMammal() && lipids >= dino.getLipids() && minerals >= dino.getMinerals() && proximates >= dino.getProximates() && vitamins >= dino.getVitamins())
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Turn one item from the cultivator source stack into the appropriate grinded item in the cultivator result stack
     */
    public void cultivate()
    {
        if (this.canCultivate())
        {
            Dinosaur dinoInEgg = JCEntityRegistry.getDinosaurById(slots[0].getItemDamage());

            waterLevel = 0;

            if (dinoInEgg != null)
            {
                lipids -= dinoInEgg.getLipids();
                minerals -= dinoInEgg.getMinerals();
                vitamins -= dinoInEgg.getVitamins();
                proximates -= dinoInEgg.getProximates();

                Class<? extends DinosaurEntity> dinoClass = dinoInEgg.getDinosaurClass();

                try
                {
                    DinosaurEntity dino = dinoClass.getConstructor(World.class).newInstance(worldObj);

                    dino.setDNAQuality(slots[0].getTagCompound().getInteger("DNAQuality"));
                    dino.setGenetics((slots[0].getTagCompound().getString("Genetics")));

                    int blockX = pos.getX();
                    int blockY = pos.getY();
                    int blockZ = pos.getZ();

                    dino.setAge(0);

                    List<CageSmallEntity> cages = worldObj.getEntitiesWithinAABB(CageSmallEntity.class, new AxisAlignedBB(blockX - 2, blockY, blockZ - 2, blockX + 2, blockY + 1, blockZ + 2));

                    CageSmallEntity cage = null;

                    for (CageSmallEntity cCage : cages)
                    {
                        if (cCage.getEntity() == null)
                        {
                            cage = cCage;
                            break;
                        }
                    }

                    if (cage != null)
                    {
                        cage.setEntity(dino);
                    }
                    else
                    {
                        dino.setLocationAndAngles(blockX + 0.5, blockY + 1, blockZ + 0.5, MathHelper.wrapAngleTo180_float(worldObj.rand.nextFloat() * 360.0F), 0.0F);
                        dino.rotationYawHead = dino.rotationYaw;
                        dino.renderYawOffset = dino.rotationYaw;

                        worldObj.spawnEntityInWorld(dino);
                    }

                    slots[0].stackSize--;

                    if (slots[0].stackSize <= 0)
                    {
                        slots[0] = null;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return index != 2;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item, side
     */
    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item, side
     */
    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        if (direction == EnumFacing.DOWN && index == 1)
        {
            Item item = stack.getItem();

            if (item != Items.water_bucket && item != Items.bucket)
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getGuiID()
    {
        return JurassiCraft.MODID + ":cultivator";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new DNASynthesizerContainer(playerInventory, this);
    }

    @Override
    public int getField(int id)
    {
        switch (id)
        {
            case 0:
                return this.cultivateTime;
            case 1:
                return this.totalCultivateTime;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value)
    {
        switch (id)
        {
            case 0:
                this.cultivateTime = value;
                break;
            case 1:
                this.totalCultivateTime = value;
        }
    }

    @Override
    public int getFieldCount()
    {
        return 2;
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < this.slots.length; ++i)
        {
            this.slots[i] = null;
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), compound);
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SPacketUpdateTileEntity packet)
    {
        NBTTagCompound compound = packet.getNbtCompound();
        this.readFromNBT(compound);
    }

    public int getWaterLevel()
    {
        return waterLevel;
    }

    public int getProximates()
    {
        return proximates;
    }

    public int getLipids()
    {
        return lipids;
    }

    public int getVitamins()
    {
        return vitamins;
    }

    public int getMinerals()
    {
        return minerals;
    }

    public int getMaxNutrients()
    {
        return maxNutrients;
    }

    public Dinosaur getDinosaur()
    {
        if (slots[0] != null)
        {
            return JCEntityRegistry.getDinosaurById(slots[0].getItemDamage());
        }

        return null;
    }
}
