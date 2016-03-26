package org.jurassicraft.server.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.server.block.BlockHandler;
import org.jurassicraft.server.creativetab.TabHandler;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.base.EntityHandler;
import org.jurassicraft.server.lang.AdvLang;
import org.jurassicraft.server.tileentity.ActionFigureTile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemActionFigure extends Item
{
    public ItemActionFigure()
    {
        super();

        this.setCreativeTab(TabHandler.INSTANCE.merchandise);
        this.setHasSubtypes(true);
    }

    /**
     * Called when a Block is right-clicked with this Item
     *
     * @param pos  The block being right-clicked
     * @param side The side being right-clicked
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        pos = pos.offset(side);

        if (playerIn.canPlayerEdit(pos, side, stack))
        {
            Block block = BlockHandler.INSTANCE.action_figure;

            if (block.canPlaceBlockAt(worldIn, pos))
            {
                IBlockState state = block.getDefaultState();
                worldIn.setBlockState(pos, block.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn));
                block.onBlockPlacedBy(worldIn, pos, state, playerIn, stack);

                ActionFigureTile tile = (ActionFigureTile) worldIn.getTileEntity(pos);
                tile.setDinosaur(stack.getItemDamage());

                if (!playerIn.capabilities.isCreativeMode)
                {
                    stack.stackSize--;
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        String dinoName = getDinosaur(stack).getName().toLowerCase().replaceAll(" ", "_");

        return new AdvLang("item.action_figure.name").withProperty("dino", "entity.jurassicraft." + dinoName + ".name").build();
    }

    public Dinosaur getDinosaur(ItemStack stack)
    {
        return EntityHandler.INSTANCE.getDinosaurById(stack.getMetadata());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subtypes)
    {
        List<Dinosaur> dinosaurs = new ArrayList<Dinosaur>(EntityHandler.INSTANCE.getDinosaurs());

        Map<Dinosaur, Integer> ids = new HashMap<Dinosaur, Integer>();

        for (Dinosaur dino : dinosaurs)
        {
            ids.put(dino, EntityHandler.INSTANCE.getDinosaurId(dino));
        }

        Collections.sort(dinosaurs);

        for (Dinosaur dino : dinosaurs)
        {
            if (dino.shouldRegister())
            {
                subtypes.add(new ItemStack(item, 1, ids.get(dino)));
            }
        }
    }
}
