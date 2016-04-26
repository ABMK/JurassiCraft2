package org.jurassicraft.server.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import org.jurassicraft.server.genetics.GeneticsHelper;
import org.jurassicraft.server.lang.AdvLang;

import java.util.List;

public class DNAContainerItem extends Item
{
    public int getContainerId(ItemStack stack)
    {
        return 0;
    }

    public int getDNAQuality(EntityPlayer player, ItemStack stack)
    {
        int quality = player.capabilities.isCreativeMode ? 100 : 0;

        NBTTagCompound nbt = stack.getTagCompound();

        if (nbt == null)
        {
            nbt = new NBTTagCompound();
        }

        if (nbt.hasKey("DNAQuality"))
        {
            quality = nbt.getInteger("DNAQuality");
        }
        else
        {
            nbt.setInteger("DNAQuality", quality);
        }

        stack.setTagCompound(nbt);

        return quality;
    }

    public String getGeneticCode(EntityPlayer player, ItemStack stack)
    {
        NBTTagCompound nbt = stack.getTagCompound();

        String genetics = GeneticsHelper.randomGenetics(player.worldObj.rand);

        if (nbt == null)
        {
            nbt = new NBTTagCompound();
        }

        if (nbt.hasKey("Genetics"))
        {
            genetics = nbt.getString("Genetics");
        }
        else
        {
            nbt.setString("Genetics", genetics);
        }

        stack.setTagCompound(nbt);

        return genetics;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> lore, boolean advanced)
    {
        int quality = getDNAQuality(player, stack);

        EnumChatFormatting colour;

        if (quality > 75)
        {
            colour = EnumChatFormatting.GREEN;
        }
        else if (quality > 50)
        {
            colour = EnumChatFormatting.YELLOW;
        }
        else if (quality > 25)
        {
            colour = EnumChatFormatting.GOLD;
        }
        else
        {
            colour = EnumChatFormatting.RED;
        }

        lore.add(colour + new AdvLang("lore.dna_quality.name").withProperty("quality", quality + "").build());
        lore.add(EnumChatFormatting.BLUE + new AdvLang("lore.genetic_code.name").withProperty("code", getGeneticCode(player, stack).toString()).build());
    }
}
