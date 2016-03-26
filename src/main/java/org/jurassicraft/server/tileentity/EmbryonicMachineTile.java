package org.jurassicraft.server.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.container.EmbryonicMachineContainer;
import org.jurassicraft.server.item.ItemDNA;
import org.jurassicraft.server.item.JCItemRegistry;
import org.jurassicraft.server.item.PlantDNAItem;

public class EmbryonicMachineTile extends MachineBaseTile
{
    private int[] inputs = new int[] { 0, 1, 2 };
    private int[] outputs = new int[] { 3, 4, 5, 6 };

    private ItemStack[] slots = new ItemStack[7];

    @Override
    protected int getProcess(int slot)
    {
        return 0;
    }

    @Override
    protected boolean canProcess(int process)
    {
        ItemStack dna = slots[0];
        ItemStack petridish = slots[1];
        ItemStack syringe = slots[2];

        if (dna != null && petridish != null && syringe != null && syringe.getItem() == JCItemRegistry.empty_syringe)
        {
            ItemStack output = null;

            if (petridish.getItem() == JCItemRegistry.petri_dish && dna.getItem() instanceof ItemDNA)
            {
                output = new ItemStack(JCItemRegistry.syringe, 1, dna.getItemDamage());
                output.setTagCompound(dna.getTagCompound());
            }
            else if (petridish.getItem() == JCItemRegistry.plant_cells_petri_dish && dna.getItem() instanceof PlantDNAItem)
            {
                output = new ItemStack(JCItemRegistry.plant_callus, 1, dna.getItemDamage());
                output.setTagCompound(dna.getTagCompound());
            }

            return output != null && hasOutputSlot(output);
        }

        return false;
    }

    @Override
    protected void processItem(int process)
    {
        if (this.canProcess(process))
        {
            ItemStack output = null;

            if (slots[0].getItem() instanceof ItemDNA && slots[1].getItem() == JCItemRegistry.petri_dish)
            {
                output = new ItemStack(JCItemRegistry.syringe, 1, slots[0].getItemDamage());
            }
            else if (slots[0].getItem() instanceof PlantDNAItem && slots[1].getItem() == JCItemRegistry.plant_cells_petri_dish)
            {
                output = new ItemStack(JCItemRegistry.plant_callus, 1, slots[0].getItemDamage());
            }

            output.setTagCompound(slots[0].getTagCompound());

            int emptySlot = getOutputSlot(output);

            if (emptySlot != -1)
            {
                mergeStack(emptySlot, output);

                decreaseStackSize(0);
                decreaseStackSize(1);
                decreaseStackSize(2);
            }
        }
    }

    @Override
    protected int getMainOutput(int process)
    {
        return 1;
    }

    @Override
    protected int getStackProcessTime(ItemStack stack)
    {
        return 200;
    }

    @Override
    protected int getProcessCount()
    {
        return 1;
    }

    @Override
    protected int[] getInputs()
    {
        return inputs;
    }

    @Override
    protected int[] getInputs(int process)
    {
        return getInputs();
    }

    @Override
    protected int[] getOutputs()
    {
        return outputs;
    }

    @Override
    protected ItemStack[] getSlots()
    {
        return slots;
    }

    @Override
    protected void setSlots(ItemStack[] slots)
    {
        this.slots = slots;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new EmbryonicMachineContainer(playerInventory, this);
    }

    @Override
    public String getGuiID()
    {
        return JurassiCraft.MODID + ":embryonic_machine";
    }

    @Override
    public String getName()
    {
        return hasCustomName() ? customName : "container.embryonic_machine";
    }

    public String getCommandSenderName() // Forge Version compatibility, keep both getName and getCommandSenderName
    {
        return getName();
    }
}
