package org.jurassicraft.server.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.server.api.CleanableItem;
import org.jurassicraft.server.api.SubBlocksBlock;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.base.EntityHandler;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.item.itemblock.EncasedFossilItemBlock;
import org.jurassicraft.server.tab.TabHandler;

import java.util.List;
import java.util.Random;

public class EncasedFossilBlock extends Block implements SubBlocksBlock, CleanableItem
{
    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 15);

    private int start;

    public EncasedFossilBlock(int start)
    {
        super(Material.ROCK);
        this.setHardness(2.0F);
        this.setResistance(8.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(TabHandler.INSTANCE.fossils);

        this.start = start;

        this.setDefaultState(blockState.getBaseState().withProperty(VARIANT, 0));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(VARIANT);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    protected ItemStack createStackedBlock(IBlockState state)
    {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
    {
        List<Dinosaur> dinosaurs = EntityHandler.INSTANCE.getDinosaurs();

        for (int i = 0; i < 16; i++)
        {
            int dinoIndex = i + start;

            if (dinoIndex >= dinosaurs.size())
            {
                break;
            }

            if (dinosaurs.get(dinoIndex).shouldRegister())
            {
                list.add(new ItemStack(this, 1, i));
            }
        }
    }

    public Dinosaur getDinosaur(int metadata)
    {
        return EntityHandler.INSTANCE.getDinosaurById(start + metadata);
    }

    @Override
    public ItemBlock getItemBlock()
    {
        return new EncasedFossilItemBlock(this);
    }

    @Override
    public String getHarvestTool(IBlockState state)
    {
        return "pickaxe";
    }

    @Override
    public int getHarvestLevel(IBlockState state)
    {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion)
    {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isCleanable(ItemStack stack)
    {
        return true;
    }

    @Override
    public ItemStack getCleanedItem(ItemStack stack, Random random)
    {
        int dinosaurId = BlockHandler.INSTANCE.getDinosaurId((EncasedFossilBlock) Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
        String[] bones = EntityHandler.INSTANCE.getDinosaurById(dinosaurId).getBones();
        return new ItemStack(ItemHandler.INSTANCE.FOSSILS.get(bones[random.nextInt(bones.length)]), 1, dinosaurId);
    }
}
