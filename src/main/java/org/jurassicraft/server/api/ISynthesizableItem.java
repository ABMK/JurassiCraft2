package org.jurassicraft.server.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Random;

public interface ISynthesizableItem
{
    boolean isSynthesizable(ItemStack stack);

    ItemStack getSynthesizedItem(ItemStack stack, Random random);

    static ISynthesizableItem getSynthesizableItem(ItemStack stack)
    {
        if (stack != null)
        {
            Item item = stack.getItem();

            if (item instanceof ItemBlock)
            {
                Block block = ((ItemBlock) item).getBlock();

                if (block instanceof ISynthesizableItem)
                {
                    return (ISynthesizableItem) block;
                }
            }
            else if (item instanceof ISynthesizableItem)
            {
                return (ISynthesizableItem) item;
            }
        }

        return null;
    }

    static boolean isSynthesizableItem(ItemStack stack)
    {
        return getSynthesizableItem(stack) != null;
    }
}
