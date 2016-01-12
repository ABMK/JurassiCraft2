package net.timeless.animationapi.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ilexiconn.llibrary.client.model.entity.animation.IModelAnimator;
import net.ilexiconn.llibrary.client.model.modelbase.MowzieModelRenderer;
import net.ilexiconn.llibrary.client.model.tabula.ModelJson;
import net.ilexiconn.llibrary.common.animation.Animation;
import net.ilexiconn.llibrary.common.map.ListHashMap;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.timeless.animationapi.TabulaModelHelper;
import net.timeless.animationapi.client.dto.AnimationsDTO;
import net.timeless.animationapi.client.dto.DinosaurRenderDefDTO;
import net.timeless.animationapi.client.dto.PoseDTO;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.model.ModelDinosaur;
import org.jurassicraft.common.dinosaur.Dinosaur;
import org.jurassicraft.common.entity.base.EntityDinosaur;
import org.jurassicraft.common.entity.base.EnumGrowthStage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@SideOnly(Side.CLIENT)
public abstract class DinosaurAnimator implements IModelAnimator
{
    private static class PreloadedModelData
    {
        public PreloadedModelData()
        {
            this(null, null);
        }

        public PreloadedModelData(MowzieModelRenderer[][] renderers, Map<Animation, int[][]> animations)
        {
            if (renderers == null)
            {
                renderers = new MowzieModelRenderer[0][];
            }

            if (animations == null)
            {
                animations = new LinkedHashMap<Animation, int[][]>();
            }

            this.models = renderers;
            this.animations = animations;
        }

        MowzieModelRenderer[][] models;
        Map<Animation, int[][]> animations;
    }

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(DinosaurRenderDefDTO.class, new DinosaurRenderDefDTO.DinosaurDeserializer()).create();

    private Map<EnumGrowthStage, PreloadedModelData> modelData;
    protected Map<Integer, Map<EnumGrowthStage, JabelarAnimationHelper>> entityIDToAnimation = new HashMap<Integer, Map<EnumGrowthStage, JabelarAnimationHelper>>();

    /**
     * Loads the model, etc... for the dinosaur given.
     *
     * @param dino the dinosaur to animate
     */
    public DinosaurAnimator(Dinosaur dino)
    {
        String name = dino.getName().toLowerCase(); // this should match name of
        // your resource package and
        // files
        this.modelData = new EnumMap<EnumGrowthStage, PreloadedModelData>(EnumGrowthStage.class);
        URI dinoDirURI;

        try
        {
            dinoDirURI = new URI("/assets/jurassicraft/models/entities/" + name + "/");
        }
        catch (URISyntaxException urise)
        {
            JurassiCraft.instance.getLogger().fatal("Illegal URI /assets/jurassicraft/models/entities/" + name + "/", urise);
            return;
        }

        for (EnumGrowthStage growth : EnumGrowthStage.values())
        {
            try
            {
                EnumGrowthStage fileGrowthStage = growth;

                if (!dino.useAllGrowthStages())
                {
                    if (growth == EnumGrowthStage.ADOLESCENT)
                    {
                        fileGrowthStage = EnumGrowthStage.ADULT;
                    }
                    else if (growth == EnumGrowthStage.JUVENILE)
                    {
                        fileGrowthStage = EnumGrowthStage.INFANT;
                    }
                }

                this.modelData.put(growth, loadDinosaur(dinoDirURI, name, fileGrowthStage));
            }
            catch (Exception e)
            {
                // TODO: should this be caught here? We can't continue, because
                // it breaks the contract that every ...
                // model has at least the IDLE sequence defined
                JurassiCraft.instance.getLogger().fatal("Failed to parse growth state " + growth + " for dinosaur " + name, e);
                this.modelData.put(growth, new PreloadedModelData());
            }
        }
    }

    /**
     * Loads a specific growth state
     *
     * @param dinoDir the base directory
     * @param name    the name of the dino
     * @param growth  the growthstate to load
     * @throws IOException
     */
    private static PreloadedModelData loadDinosaur(URI dinoDir, String name, EnumGrowthStage growth) throws IOException
    {
        String growthName = growth.name().toLowerCase(Locale.ROOT);
        URI growthSensitiveDir = dinoDir.resolve(growthName + "/");
        URI definitionFile = growthSensitiveDir.resolve(name + "_" + growthName + ".json");
        InputStream dinoDef = TabulaModelHelper.class.getResourceAsStream(definitionFile.toString());

        if (dinoDef == null)
        {
            throw new IllegalArgumentException("No model definition for the dino " + name + " with grow-state " + growth + " exists. Expected at " + definitionFile);
        }

        try
        {
            Reader reader = new InputStreamReader(dinoDef);
            AnimationsDTO rawAnimations = GSON.fromJson(reader, AnimationsDTO.class);
            PreloadedModelData data = getPosedModels(growthSensitiveDir, rawAnimations);
            JurassiCraft.instance.getLogger().debug("Successfully loaded " + name + "(" + growth + ") from " + definitionFile);

            reader.close();

            return data;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the posed models from the set of animations defined. Illegal poses (e.g. where the file doesn't exist) will be skipped and not show up in the map.
     *
     * @param anims the read animations
     * @returns the posed models
     */
    private static PreloadedModelData getPosedModels(URI dinoDirURI, AnimationsDTO anims)
    {
        // Check if the file is legal: -> at least one pose for the IDLE
        // animation
        if (anims == null || anims.poses == null || anims.poses.get(Animations.IDLE.name()) == null || anims.poses.get(Animations.IDLE.name()).length == 0)
        {
            throw new IllegalArgumentException("Animation files must define at least one pose for the IDLE animation");
        }
        // Collect all needed resources
        List<String> posedModelResources = new ArrayList<String>();
        for (PoseDTO[] poses : anims.poses.values())
        {
            if (poses == null)
            {
                continue; // Pending comma in the map, ignoring
            }
            for (PoseDTO pose : poses)
            {
                if (pose == null)
                {
                    continue; // Pending comma in the list, ignoring
                }
                if (pose.pose == null)
                {
                    throw new IllegalArgumentException("Every pose must define a pose file");
                }
                String resolvedRes = resolve(dinoDirURI, pose.pose);
                int index = posedModelResources.indexOf(resolvedRes);
                if (index == -1)
                { // Not in the list
                    pose.index = posedModelResources.size();
                    posedModelResources.add(resolvedRes);
                }
                else
                { // Already in there
                    pose.index = index;
                }
            }
        }
        assert (posedModelResources.size() > 0); // anims.poses.get(Animations.IDLE.get().getId()).length
        // != 0
        MowzieModelRenderer[][] posedCubes = new MowzieModelRenderer[posedModelResources.size()][];
        Map<Animation, int[][]> animationSequences = new ListHashMap<Animation, int[][]>();
        // find all names we need
        ModelDinosaur mainModel = JabelarAnimationHelper.getTabulaModel(posedModelResources.get(0), 0);
        if (mainModel == null)
        {
            throw new IllegalArgumentException("Couldn't load the model from " + posedModelResources.get(0));
        }
        String[] cubeNameArray = mainModel.getCubeNamesArray();
        int numParts = cubeNameArray.length;
        // load the models from the resource files
        for (int i = 0; i < posedModelResources.size(); i++)
        {
            String resource = posedModelResources.get(i);
            ModelDinosaur theModel = JabelarAnimationHelper.getTabulaModel(resource, 0);
            if (theModel == null)
            {
                throw new IllegalArgumentException("Couldn't load the model from " + resource);
            }
            MowzieModelRenderer[] cubes = new MowzieModelRenderer[numParts];
            for (int partIndex = 0; partIndex < numParts; partIndex++)
            {
                String cubeName = cubeNameArray[partIndex];
                MowzieModelRenderer cube = theModel.getCube(cubeName);
                if (cube == null)
                {
                    throw new IllegalArgumentException("Could not retrieve cube " + cubeName + " (" + partIndex + ") from the model " + resource);
                }
                cubes[partIndex] = cube;
            }
            posedCubes[i] = cubes;
        }
        // load the animations sequences
        for (Map.Entry<String, PoseDTO[]> entry : anims.poses.entrySet())
        {
            Animation animations = Animations.valueOf(entry.getKey()).get();
            PoseDTO[] poses = entry.getValue();
            int[][] poseSequence = new int[poses.length][2];
            for (int i = 0; i < poses.length; i++)
            {
                poseSequence[i][0] = poses[i].index;
                poseSequence[i][1] = poses[i].time;
            }
            animationSequences.put(animations, poseSequence);
        }
        return new PreloadedModelData(posedCubes, animationSequences);
    }

    private static String resolve(URI dinoDirURI, String posePath)
    {
        URI uri = dinoDirURI.resolve(posePath);
        return uri.toString();
    }

    private JabelarAnimationHelper getAnimationHelper(EntityDinosaur entity, ModelDinosaur model)
    {
        Integer id = entity.getEntityId();
        EnumGrowthStage growth = entity.getGrowthStage();
        Map<EnumGrowthStage, JabelarAnimationHelper> growthToRender = entityIDToAnimation.get(id);

        if (growthToRender == null)
        {
            growthToRender = new EnumMap<EnumGrowthStage, JabelarAnimationHelper>(EnumGrowthStage.class);
            entityIDToAnimation.put(id, growthToRender);
        }

        JabelarAnimationHelper render = growthToRender.get(growth);

        if (render == null)
        {
            PreloadedModelData growthModel = modelData.get(growth);
            int cubes = growthModel.models.length > 0 ? growthModel.models[0].length : 0;
            render = new JabelarAnimationHelper(entity, model, cubes, growthModel.models, growthModel.animations, true);
            growthToRender.put(growth, render);
        }

        return render;
    }

    @Override
    public final void setRotationAngles(ModelJson model, float limbSwing, float limbSwingAmount, float rotation, float rotationYaw, float rotationPitch, float partialTicks, Entity entity)
    {
        ModelDinosaur theModel = (ModelDinosaur) model;
        EntityDinosaur theEntity = (EntityDinosaur) entity;
        // assert(size == 1/16f); // Ignore the size

        setRotationAngles(theModel, limbSwing, limbSwingAmount, rotation, rotationYaw, rotationPitch, partialTicks, theEntity);
    }

    protected void setRotationAngles(ModelDinosaur model, float limbSwing, float limbSwingAmount, float rotation, float rotationYaw, float rotationPitch, float partialTick, EntityDinosaur entity)
    {
        getAnimationHelper(entity, model).performJabelarAnimations(partialTick);
        if (entity.getAnimation() != Animations.DYING.get()) // still alive
        {
            if (entity.isSwimming())
            {
                performMowzieSwimmingAnimations(model, limbSwing, limbSwingAmount, rotation, rotationYaw, rotationPitch, partialTick, entity);
            }
            else
            {
                performMowzieLandAnimations(model, limbSwing, limbSwingAmount, rotation, rotationYaw, rotationPitch, partialTick, entity);
            }
        }
    }

    protected void performMowzieLandAnimations(ModelDinosaur parModel, float parLimbSwing, float parLimbSwingAmount, float parRotation, float parRotationYaw, float parRotationPitch, float parPartialTicks, EntityDinosaur parEntity)
    {
    }

    /*
     * @Override this if you want swimming dino to have different cyclical animations.
     */
    protected void performMowzieSwimmingAnimations(ModelDinosaur parModel, float parLimbSwing, float parLimbSwingAmount, float parRotation, float parRotationYaw, float parRotationPitch, float parPartialTicks, EntityDinosaur parEntity)
    {
        performMowzieLandAnimations(parModel, parLimbSwing, parLimbSwingAmount, parRotation, parRotationYaw, parRotationPitch, parPartialTicks, parEntity);
    }
}
