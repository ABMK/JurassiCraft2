package org.jurassicraft.server.block.tree;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.server.creativetab.JCCreativeTabs;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class JCStairsBlock extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum HALF = PropertyEnum.create("half", JCStairsBlock.EnumHalf.class);
    public static final PropertyEnum SHAPE = PropertyEnum.create("shape", JCStairsBlock.EnumShape.class);
    private static final int[][] field_150150_a = new int[][] { { 4, 5 }, { 5, 7 }, { 6, 7 }, { 4, 6 }, { 0, 1 }, { 1, 3 }, { 2, 3 }, { 0, 2 } };
    private final Block modelBlock;
    private final IBlockState modelState;
    private boolean hasRaytraced;
    private int rayTracePass;

    public JCStairsBlock(TreeType type, IBlockState modelState)
    {
        super(modelState.getBlock().getMaterial());
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, JCStairsBlock.EnumHalf.BOTTOM).withProperty(SHAPE, JCStairsBlock.EnumShape.STRAIGHT));
        this.modelBlock = modelState.getBlock();
        this.modelState = modelState;
        this.setHardness(this.modelBlock.getBlockHardness(null, null));
        this.setResistance((this.modelBlock.getExplosionResistance(null) * 5.0F) / 3.0F);
        this.setStepSound(this.modelBlock.stepSound);
        this.setHarvestLevel(this.modelBlock.getHarvestTool(this.modelState), this.modelBlock.getHarvestLevel(this.modelState));
        this.setLightOpacity(255);
        this.setCreativeTab(JCCreativeTabs.plants);
        this.setUnlocalizedName(type.name().toLowerCase().replaceAll(" ", "_") + "_stairs");
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        if (this.hasRaytraced)
        {
            this.setBlockBounds(0.5F * (float) (this.rayTracePass % 2), 0.5F * (float) (this.rayTracePass / 4 % 2), 0.5F * (float) (this.rayTracePass / 2 % 2), 0.5F + 0.5F * (float) (this.rayTracePass % 2), 0.5F + 0.5F * (float) (this.rayTracePass / 4 % 2), 0.5F + 0.5F * (float) (this.rayTracePass / 2 % 2));
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean isFullCube()
    {
        return false;
    }

    /**
     * Set the block bounds as the collision bounds for the stairs at the given position
     */
    public void setBaseCollisionBounds(IBlockAccess worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos).getValue(HALF) == JCStairsBlock.EnumHalf.TOP)
        {
            this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    /**
     * Checks if a block is stairs
     */
    public static boolean isStairs(Block block)
    {
        return block instanceof JCStairsBlock;
    }

    /**
     * Check whether there is a stair block at the given position and it has the same properties as the given BlockState
     */
    public static boolean isSameStair(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState iblockstate1 = worldIn.getBlockState(pos);
        Block block = iblockstate1.getBlock();
        /**
         * Checks if a block is stairs
         */
        return isStairs(block) && iblockstate1.getValue(HALF) == state.getValue(HALF) && iblockstate1.getValue(FACING) == state.getValue(FACING);
    }

    public int func_176307_f(IBlockAccess world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        EnumFacing facing = (EnumFacing) iblockstate.getValue(FACING);
        JCStairsBlock.EnumHalf enumhalf = (JCStairsBlock.EnumHalf) iblockstate.getValue(HALF);
        boolean isTop = enumhalf == JCStairsBlock.EnumHalf.TOP;
        IBlockState iblockstate1;
        Block block;
        EnumFacing enumfacing1;

        if (facing == EnumFacing.EAST)
        {
            iblockstate1 = world.getBlockState(pos.east());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(world, pos.south(), iblockstate))
                {
                    return isTop ? 1 : 2;
                }

                if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(world, pos.north(), iblockstate))
                {
                    return isTop ? 2 : 1;
                }
            }
        }
        else if (facing == EnumFacing.WEST)
        {
            iblockstate1 = world.getBlockState(pos.west());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(world, pos.south(), iblockstate))
                {
                    return isTop ? 2 : 1;
                }

                if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(world, pos.north(), iblockstate))
                {
                    return isTop ? 1 : 2;
                }
            }
        }
        else if (facing == EnumFacing.SOUTH)
        {
            iblockstate1 = world.getBlockState(pos.south());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.WEST && !isSameStair(world, pos.east(), iblockstate))
                {
                    return isTop ? 2 : 1;
                }

                if (enumfacing1 == EnumFacing.EAST && !isSameStair(world, pos.west(), iblockstate))
                {
                    return isTop ? 1 : 2;
                }
            }
        }
        else if (facing == EnumFacing.NORTH)
        {
            iblockstate1 = world.getBlockState(pos.north());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.WEST && !isSameStair(world, pos.east(), iblockstate))
                {
                    return isTop ? 1 : 2;
                }

                if (enumfacing1 == EnumFacing.EAST && !isSameStair(world, pos.west(), iblockstate))
                {
                    return isTop ? 2 : 1;
                }
            }
        }

        return 0;
    }

    public int func_176305_g(IBlockAccess world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        EnumFacing enumfacing = (EnumFacing) iblockstate.getValue(FACING);
        JCStairsBlock.EnumHalf enumhalf = (JCStairsBlock.EnumHalf) iblockstate.getValue(HALF);
        boolean flag = enumhalf == JCStairsBlock.EnumHalf.TOP;
        IBlockState iblockstate1;
        Block block;
        EnumFacing enumfacing1;

        if (enumfacing == EnumFacing.EAST)
        {
            iblockstate1 = world.getBlockState(pos.west());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(world, pos.north(), iblockstate))
                {
                    return flag ? 1 : 2;
                }

                if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(world, pos.south(), iblockstate))
                {
                    return flag ? 2 : 1;
                }
            }
        }
        else if (enumfacing == EnumFacing.WEST)
        {
            iblockstate1 = world.getBlockState(pos.east());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(world, pos.north(), iblockstate))
                {
                    return flag ? 2 : 1;
                }

                if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(world, pos.south(), iblockstate))
                {
                    return flag ? 1 : 2;
                }
            }
        }
        else if (enumfacing == EnumFacing.SOUTH)
        {
            iblockstate1 = world.getBlockState(pos.north());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.WEST && !isSameStair(world, pos.west(), iblockstate))
                {
                    return flag ? 2 : 1;
                }

                if (enumfacing1 == EnumFacing.EAST && !isSameStair(world, pos.east(), iblockstate))
                {
                    return flag ? 1 : 2;
                }
            }
        }
        else if (enumfacing == EnumFacing.NORTH)
        {
            iblockstate1 = world.getBlockState(pos.south());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.WEST && !isSameStair(world, pos.west(), iblockstate))
                {
                    return flag ? 1 : 2;
                }

                if (enumfacing1 == EnumFacing.EAST && !isSameStair(world, pos.east(), iblockstate))
                {
                    return flag ? 2 : 1;
                }
            }
        }

        return 0;
    }

    public boolean func_176306_h(IBlockAccess world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        EnumFacing enumfacing = (EnumFacing) iblockstate.getValue(FACING);
        JCStairsBlock.EnumHalf enumhalf = (JCStairsBlock.EnumHalf) iblockstate.getValue(HALF);
        boolean flag = enumhalf == JCStairsBlock.EnumHalf.TOP;
        float f = 0.5F;
        float f1 = 1.0F;

        if (flag)
        {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.5F;
        boolean flag1 = true;
        IBlockState iblockstate1;
        Block block;
        EnumFacing enumfacing1;

        if (enumfacing == EnumFacing.EAST)
        {
            f2 = 0.5F;
            f5 = 1.0F;
            iblockstate1 = world.getBlockState(pos.east());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(world, pos.south(), iblockstate))
                {
                    f5 = 0.5F;
                    flag1 = false;
                }
                else if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(world, pos.north(), iblockstate))
                {
                    f4 = 0.5F;
                    flag1 = false;
                }
            }
        }
        else if (enumfacing == EnumFacing.WEST)
        {
            f3 = 0.5F;
            f5 = 1.0F;
            iblockstate1 = world.getBlockState(pos.west());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(world, pos.south(), iblockstate))
                {
                    f5 = 0.5F;
                    flag1 = false;
                }
                else if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(world, pos.north(), iblockstate))
                {
                    f4 = 0.5F;
                    flag1 = false;
                }
            }
        }
        else if (enumfacing == EnumFacing.SOUTH)
        {
            f4 = 0.5F;
            f5 = 1.0F;
            iblockstate1 = world.getBlockState(pos.south());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.WEST && !isSameStair(world, pos.east(), iblockstate))
                {
                    f3 = 0.5F;
                    flag1 = false;
                }
                else if (enumfacing1 == EnumFacing.EAST && !isSameStair(world, pos.west(), iblockstate))
                {
                    f2 = 0.5F;
                    flag1 = false;
                }
            }
        }
        else if (enumfacing == EnumFacing.NORTH)
        {
            iblockstate1 = world.getBlockState(pos.north());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.WEST && !isSameStair(world, pos.east(), iblockstate))
                {
                    f3 = 0.5F;
                    flag1 = false;
                }
                else if (enumfacing1 == EnumFacing.EAST && !isSameStair(world, pos.west(), iblockstate))
                {
                    f2 = 0.5F;
                    flag1 = false;
                }
            }
        }

        this.setBlockBounds(f2, f, f4, f3, f1, f5);
        return flag1;
    }

    public boolean func_176304_i(IBlockAccess world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        EnumFacing enumfacing = (EnumFacing) iblockstate.getValue(FACING);
        JCStairsBlock.EnumHalf enumhalf = (JCStairsBlock.EnumHalf) iblockstate.getValue(HALF);
        boolean flag = enumhalf == JCStairsBlock.EnumHalf.TOP;
        float f = 0.5F;
        float f1 = 1.0F;

        if (flag)
        {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = 0.5F;
        float f5 = 1.0F;
        boolean flag1 = false;
        IBlockState iblockstate1;
        Block block;
        EnumFacing enumfacing1;

        if (enumfacing == EnumFacing.EAST)
        {
            iblockstate1 = world.getBlockState(pos.west());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(world, pos.north(), iblockstate))
                {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag1 = true;
                }
                else if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(world, pos.south(), iblockstate))
                {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag1 = true;
                }
            }
        }
        else if (enumfacing == EnumFacing.WEST)
        {
            iblockstate1 = world.getBlockState(pos.east());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                f2 = 0.5F;
                f3 = 1.0F;
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.NORTH && !isSameStair(world, pos.north(), iblockstate))
                {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag1 = true;
                }
                else if (enumfacing1 == EnumFacing.SOUTH && !isSameStair(world, pos.south(), iblockstate))
                {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag1 = true;
                }
            }
        }
        else if (enumfacing == EnumFacing.SOUTH)
        {
            iblockstate1 = world.getBlockState(pos.north());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                f4 = 0.0F;
                f5 = 0.5F;
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.WEST && !isSameStair(world, pos.west(), iblockstate))
                {
                    flag1 = true;
                }
                else if (enumfacing1 == EnumFacing.EAST && !isSameStair(world, pos.east(), iblockstate))
                {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag1 = true;
                }
            }
        }
        else if (enumfacing == EnumFacing.NORTH)
        {
            iblockstate1 = world.getBlockState(pos.south());
            block = iblockstate1.getBlock();

            if (isStairs(block) && enumhalf == iblockstate1.getValue(HALF))
            {
                enumfacing1 = (EnumFacing) iblockstate1.getValue(FACING);

                if (enumfacing1 == EnumFacing.WEST && !isSameStair(world, pos.west(), iblockstate))
                {
                    flag1 = true;
                }
                else if (enumfacing1 == EnumFacing.EAST && !isSameStair(world, pos.east(), iblockstate))
                {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag1 = true;
                }
            }
        }

        if (flag1)
        {
            this.setBlockBounds(f2, f, f4, f3, f1, f5);
        }

        return flag1;
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     *
     * @param collidingEntity the Entity colliding with this Block
     */
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        this.setBaseCollisionBounds(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        boolean flag = this.func_176306_h(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);

        if (flag && this.func_176304_i(worldIn, pos))
        {
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        this.modelBlock.onBlockClicked(worldIn, pos, playerIn);
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        this.modelBlock.randomDisplayTick(worldIn, pos, state, rand);
    }

    /**
     * Called when a player destroys this Block
     */
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
        this.modelBlock.onBlockDestroyedByPlayer(worldIn, pos, state);
    }

    @SideOnly(Side.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos)
    {
        return this.modelBlock.getMixedBrightnessForBlock(worldIn, pos);
    }

    /**
     * Returns how much this block can resist explosions from the passed in entity.
     */
    public float getExplosionResistance(Entity exploder)
    {
        return this.modelBlock.getExplosionResistance(exploder);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return this.modelBlock.tickRate(worldIn);
    }

    public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion)
    {
        return this.modelBlock.modifyAcceleration(worldIn, pos, entityIn, motion);
    }

    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return this.modelBlock.getBlockLayer();
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
    {
        return this.modelBlock.getSelectedBoundingBox(worldIn, pos);
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean isCollidable()
    {
        return this.modelBlock.isCollidable();
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return this.modelBlock.canCollideCheck(state, hitIfLiquid);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return this.modelBlock.canPlaceBlockAt(worldIn, pos);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.onNeighborBlockChange(worldIn, pos, this.modelState, Blocks.air);
        this.modelBlock.onBlockAdded(worldIn, pos, this.modelState);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        this.modelBlock.breakBlock(worldIn, pos, this.modelState);
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block)
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn)
    {
        this.modelBlock.onEntityCollidedWithBlock(worldIn, pos, entityIn);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        this.modelBlock.updateTick(worldIn, pos, state, rand);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return this.modelBlock.onBlockActivated(worldIn, pos, this.modelState, playerIn, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
    }

    /**
     * Called when this Block is destroyed by an Explosion
     */
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        this.modelBlock.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state)
    {
        return this.modelBlock.getMapColor(this.modelState);
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState iblockstate = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        iblockstate = iblockstate.withProperty(FACING, placer.getHorizontalFacing()).withProperty(SHAPE, JCStairsBlock.EnumShape.STRAIGHT);
        return facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double) hitY <= 0.5D) ? iblockstate.withProperty(HALF, JCStairsBlock.EnumHalf.BOTTOM) : iblockstate.withProperty(HALF, JCStairsBlock.EnumHalf.TOP);
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     *
     * @param start The start vector
     * @param end   The end vector
     */
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end)
    {
        MovingObjectPosition[] amovingobjectposition = new MovingObjectPosition[8];
        IBlockState iblockstate = worldIn.getBlockState(pos);
        int i = ((EnumFacing) iblockstate.getValue(FACING)).getHorizontalIndex();
        boolean flag = iblockstate.getValue(HALF) == JCStairsBlock.EnumHalf.TOP;
        int[] aint = field_150150_a[i + (flag ? 4 : 0)];
        this.hasRaytraced = true;

        for (int j = 0; j < 8; ++j)
        {
            this.rayTracePass = j;

            if (Arrays.binarySearch(aint, j) < 0)
            {
                amovingobjectposition[j] = super.collisionRayTrace(worldIn, pos, start, end);
            }
        }

        int[] aint1 = aint;
        int k = aint.length;

        for (int l = 0; l < k; ++l)
        {
            int i1 = aint1[l];
            amovingobjectposition[i1] = null;
        }

        MovingObjectPosition movingobjectposition1 = null;
        double d1 = 0.0D;
        MovingObjectPosition[] amovingobjectposition1 = amovingobjectposition;
        int j1 = amovingobjectposition.length;

        for (int k1 = 0; k1 < j1; ++k1)
        {
            MovingObjectPosition movingobjectposition = amovingobjectposition1[k1];

            if (movingobjectposition != null)
            {
                double d0 = movingobjectposition.hitVec.squareDistanceTo(end);

                if (d0 > d1)
                {
                    movingobjectposition1 = movingobjectposition;
                    d1 = d0;
                }
            }
        }

        return movingobjectposition1;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(HALF, (meta & 4) > 0 ? JCStairsBlock.EnumHalf.TOP : JCStairsBlock.EnumHalf.BOTTOM);
        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
        return iblockstate;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;

        if (state.getValue(HALF) == JCStairsBlock.EnumHalf.TOP)
        {
            i |= 4;
        }

        i |= 5 - ((EnumFacing) state.getValue(FACING)).getIndex();
        return i;
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (this.func_176306_h(worldIn, pos))
        {
            switch (this.func_176305_g(worldIn, pos))
            {
                case 0:
                    state = state.withProperty(SHAPE, JCStairsBlock.EnumShape.STRAIGHT);
                    break;
                case 1:
                    state = state.withProperty(SHAPE, JCStairsBlock.EnumShape.INNER_RIGHT);
                    break;
                case 2:
                    state = state.withProperty(SHAPE, JCStairsBlock.EnumShape.INNER_LEFT);
            }
        }
        else
        {
            switch (this.func_176307_f(worldIn, pos))
            {
                case 0:
                    state = state.withProperty(SHAPE, JCStairsBlock.EnumShape.STRAIGHT);
                    break;
                case 1:
                    state = state.withProperty(SHAPE, JCStairsBlock.EnumShape.OUTER_RIGHT);
                    break;
                case 2:
                    state = state.withProperty(SHAPE, JCStairsBlock.EnumShape.OUTER_LEFT);
            }
        }

        return state;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] { FACING, HALF, SHAPE });
    }

    public static enum EnumHalf implements IStringSerializable
    {
        TOP("top"), BOTTOM("bottom");
        private final String name;

        private EnumHalf(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }

    public static enum EnumShape implements IStringSerializable
    {
        STRAIGHT("straight"), INNER_LEFT("inner_left"), INNER_RIGHT("inner_right"), OUTER_LEFT("outer_left"), OUTER_RIGHT("outer_right");
        private final String name;

        private EnumShape(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}
