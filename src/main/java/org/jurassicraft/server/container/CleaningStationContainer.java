package org.jurassicraft.server.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.server.container.slot.FossilSlot;
import org.jurassicraft.server.container.slot.WaterBucketSlot;
import org.jurassicraft.server.item.itemblock.EncasedFossilItemBlock;
import org.jurassicraft.server.tileentity.CleaningStationTile;

public class CleaningStationContainer extends Container
{
    private final IInventory tileCleaningStation;
    private int field_178152_f;
    private int field_178153_g;
    private int field_178154_h;
    private int field_178155_i;

    public CleaningStationContainer(InventoryPlayer invPlayer, IInventory cleaningStation)
    {
        this.tileCleaningStation = cleaningStation;
        this.addSlotToContainer(new Slot(cleaningStation, 0, 56, 17));
        this.addSlotToContainer(new WaterBucketSlot(cleaningStation, 1, 56, 53));

        int i;

        for (i = 0; i < 3; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                this.addSlotToContainer(new FossilSlot(cleaningStation, i + (j * 3) + 2, i * 18 + 93 + 15, j * 18 + 26));
            }
        }

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
        }
    }

    /**
     * Add the given Listener to the list of Listeners. Method name is for legacy.
     */
    public void onCraftGuiOpened(ICrafting listener)
    {
        super.onCraftGuiOpened(listener);
        listener.sendAllWindowProperties(this, this.tileCleaningStation);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (ICrafting icrafting : this.crafters)
        {
            if (this.field_178152_f != this.tileCleaningStation.getField(2))
            {
                icrafting.sendProgressBarUpdate(this, 2, this.tileCleaningStation.getField(2));
            }

            if (this.field_178154_h != this.tileCleaningStation.getField(0))
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileCleaningStation.getField(0));
            }

            if (this.field_178155_i != this.tileCleaningStation.getField(1))
            {
                icrafting.sendProgressBarUpdate(this, 1, this.tileCleaningStation.getField(1));
            }

            if (this.field_178153_g != this.tileCleaningStation.getField(3))
            {
                icrafting.sendProgressBarUpdate(this, 3, this.tileCleaningStation.getField(3));
            }
        }

        this.field_178152_f = this.tileCleaningStation.getField(2);
        this.field_178154_h = this.tileCleaningStation.getField(0);
        this.field_178155_i = this.tileCleaningStation.getField(1);
        this.field_178153_g = this.tileCleaningStation.getField(3);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        this.tileCleaningStation.setField(id, data);
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.tileCleaningStation.isUseableByPlayer(playerIn);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack transferFrom = slot.getStack();
            itemstack = transferFrom.copy();

            if (index == 2)
            {
                if (!this.mergeItemStack(transferFrom, 3, 39, true))
                {
                    return null;
                }

                slot.onSlotChange(transferFrom, itemstack);
            }
            else if (index != 1 && index != 0)
            {
                if (transferFrom.getItem() instanceof EncasedFossilItemBlock)
                {
                    if (!this.mergeItemStack(transferFrom, 0, 1, false))
                    {
                        return null;
                    }
                }
                else if (CleaningStationTile.isItemFuel(transferFrom))
                {
                    if (!this.mergeItemStack(transferFrom, 1, 2, false))
                    {
                        return null;
                    }
                }
                else if (index >= 3 && index < 30)
                {
                    if (!this.mergeItemStack(transferFrom, 30, 39, false))
                    {
                        return null;
                    }
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(transferFrom, 3, 30, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(transferFrom, 3, 39, false))
            {
                return null;
            }

            if (transferFrom.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (transferFrom.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, transferFrom);
        }

        return itemstack;
    }
}
