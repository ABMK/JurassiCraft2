package org.jurassicraft.client.render.block;

import net.ilexiconn.llibrary.client.model.tabula.TabulaModel;
import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.block.OrientedBlock;
import org.jurassicraft.server.tile.EmbryoCalcificationMachineTile;
import org.lwjgl.opengl.GL11;

public class EmbryoCalcificationMachineSpecialRenderer extends TileEntitySpecialRenderer<EmbryoCalcificationMachineTile>
{
    private Minecraft mc = Minecraft.getMinecraft();
    private TabulaModel model;
    private TabulaModel modelWithEgg;
    private ResourceLocation texture;

    public EmbryoCalcificationMachineSpecialRenderer()
    {
        try
        {
            this.model = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/jurassicraft/models/block/embryo_calcification_machine"));
            this.modelWithEgg = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/jurassicraft/models/block/embryo_calcification_machine_egg"));
            this.texture = new ResourceLocation(JurassiCraft.MODID, "textures/blocks/embryo_calcification_machine.png");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void renderTileEntityAt(EmbryoCalcificationMachineTile tileEntity, double x, double y, double z, float p_180535_8_, int p_180535_9_)
    {
        World world = tileEntity.getWorld();

        IBlockState state = world.getBlockState(tileEntity.getPos());

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        EnumFacing value = state.getValue(OrientedBlock.FACING);

        if (value == EnumFacing.NORTH || value == EnumFacing.SOUTH)
        {
            value = value.getOpposite();
        }

        int rotation = value.getHorizontalIndex() * 90;

        GlStateManager.pushMatrix();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(x + 0.5, y + 1.5F, z + 0.5);

        GlStateManager.rotate(rotation, 0, 1, 0);

        double scale = 1.0;
        GlStateManager.scale(scale, -scale, scale);

        mc.getTextureManager().bindTexture(texture);

        ((tileEntity.getStackInSlot(1) != null || tileEntity.getStackInSlot(2) != null) ? modelWithEgg : model).render(null, 0, 0, 0, 0, 0, 0.0625F);

        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }
}
