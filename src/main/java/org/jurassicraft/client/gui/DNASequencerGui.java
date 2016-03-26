package org.jurassicraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.server.container.DNASequencerContainer;

@SideOnly(Side.CLIENT)
public class DNASequencerGui extends GuiContainer
{
    private static final ResourceLocation texture = new ResourceLocation("jurassicraft:textures/gui/dna_sequencer.png");
    /**
     * The player inventory bound to this GUI.
     */
    private final InventoryPlayer playerInventory;
    private IInventory dnaSequencer;

    public DNASequencerGui(InventoryPlayer playerInv, IInventory dnaSequencer)
    {
        super(new DNASequencerContainer(playerInv, (TileEntity) dnaSequencer));
        this.playerInventory = playerInv;
        this.dnaSequencer = dnaSequencer;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.dnaSequencer.getDisplayName().getUnformattedText();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        for (int i = 0; i < 3; i++)
        {
            int progress = this.getProgress(22, i);
            this.drawTexturedModalRect(k + 87, l + 21 + i * 20, 176, 0, progress, 6);
        }
    }

    private int getProgress(int scale, int index)
    {
        int j = this.dnaSequencer.getField(index);
        int k = this.dnaSequencer.getField(index + 3);

        return k != 0 && j != 0 ? j * scale / k : 0;
    }
}
