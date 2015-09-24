package org.jurassicraft.client.render.renderdef;

import net.minecraft.client.model.ModelBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.timeless.unilib.client.model.json.IModelAnimator;
import net.timeless.unilib.client.model.json.ModelJson;
import org.jurassicraft.client.model.animation.AnimationHypsilophodon;
import org.jurassicraft.common.entity.base.EnumGrowthStage;
import org.jurassicraft.common.entity.base.JCEntityRegistry;

@SideOnly(Side.CLIENT)
public class RenderDefHypsilophodon extends RenderDinosaurDefinition
{
    private IModelAnimator animator;
    private ModelJson model;

    public RenderDefHypsilophodon()
    {
        super(JCEntityRegistry.hypsilophodon);

        this.animator = new AnimationHypsilophodon();

        try
        {
            this.model = getDefaultTabulaModel();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public ModelBase getModel(int geneticVariant, EnumGrowthStage stage)
    {
        return model;
    }

    @Override
    public float getAdultScaleAdjustment()
    {
        return 0.65F;
    }

    @Override
    public float getBabyScaleAdjustment()
    {
        return 0.2F;
    }

    @Override
    public float getShadowSize()
    {
        return 0.65F;
    }

    public float getRenderXOffset(int geneticVariant)
    {
        return 0.0F;
    }

    public float getRenderYOffset(int geneticVariant)
    {
        return -0.05F;
    }

    public float getRenderZOffset(int geneticVariant)
    {
        return 0.0F;
    }

    @Override
    public IModelAnimator getModelAnimator(int geneticVariant)
    {
        return animator;
    }
}