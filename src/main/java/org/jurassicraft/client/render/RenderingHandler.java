package org.jurassicraft.client.render;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.model.animation.AchillobatorAnimator;
import org.jurassicraft.client.model.animation.AnkylosaurusAnimator;
import org.jurassicraft.client.model.animation.ApatosaurusAnimator;
import org.jurassicraft.client.model.animation.BaryonyxAnimator;
import org.jurassicraft.client.model.animation.BrachiosaurusAnimator;
import org.jurassicraft.client.model.animation.CarnotaurusAnimator;
import org.jurassicraft.client.model.animation.CearadactylusAnimator;
import org.jurassicraft.client.model.animation.ChasmosaurusAnimator;
import org.jurassicraft.client.model.animation.CoelacanthAnimator;
import org.jurassicraft.client.model.animation.CompsognathusAnimator;
import org.jurassicraft.client.model.animation.CorythosaurusAnimator;
import org.jurassicraft.client.model.animation.DilophosaurusAnimator;
import org.jurassicraft.client.model.animation.DimorphodonAnimator;
import org.jurassicraft.client.model.animation.DodoAnimator;
import org.jurassicraft.client.model.animation.DunkleosteusAnimator;
import org.jurassicraft.client.model.animation.EdmontosaurusAnimator;
import org.jurassicraft.client.model.animation.GallimimusAnimator;
import org.jurassicraft.client.model.animation.GiganotosaurusAnimator;
import org.jurassicraft.client.model.animation.HerrerasaurusAnimator;
import org.jurassicraft.client.model.animation.HypsilophodonAnimator;
import org.jurassicraft.client.model.animation.LambeosaurusAnimator;
import org.jurassicraft.client.model.animation.LeaellynasauraAnimator;
import org.jurassicraft.client.model.animation.LeptictidiumAnimator;
import org.jurassicraft.client.model.animation.LudodactylusAnimator;
import org.jurassicraft.client.model.animation.MajungasaurusAnimator;
import org.jurassicraft.client.model.animation.MamenchisaurusAnimator;
import org.jurassicraft.client.model.animation.MegapiranhaAnimator;
import org.jurassicraft.client.model.animation.MetriacanthosaurusAnimator;
import org.jurassicraft.client.model.animation.MicroceratusAnimator;
import org.jurassicraft.client.model.animation.MoganopterusAnimator;
import org.jurassicraft.client.model.animation.OrnithomimusAnimator;
import org.jurassicraft.client.model.animation.OthnieliaAnimator;
import org.jurassicraft.client.model.animation.PachycephalosaurusAnimator;
import org.jurassicraft.client.model.animation.ParasaurolophusAnimator;
import org.jurassicraft.client.model.animation.ProtoceratopsAnimator;
import org.jurassicraft.client.model.animation.PteranodonAnimator;
import org.jurassicraft.client.model.animation.RugopsAnimator;
import org.jurassicraft.client.model.animation.SegisaurusAnimator;
import org.jurassicraft.client.model.animation.SpinosaurusAnimator;
import org.jurassicraft.client.model.animation.StegosaurusAnimator;
import org.jurassicraft.client.model.animation.TherizinosaurusAnimator;
import org.jurassicraft.client.model.animation.TriceratopsAnimator;
import org.jurassicraft.client.model.animation.TroodonAnimator;
import org.jurassicraft.client.model.animation.TropeognathusAnimator;
import org.jurassicraft.client.model.animation.TylosaurusAnimator;
import org.jurassicraft.client.model.animation.TyrannosaurusAnimator;
import org.jurassicraft.client.model.animation.VelociraptorAnimator;
import org.jurassicraft.client.model.animation.VelociraptorBlueAnimator;
import org.jurassicraft.client.model.animation.VelociraptorCharlieAnimator;
import org.jurassicraft.client.model.animation.VelociraptorDeltaAnimator;
import org.jurassicraft.client.model.animation.VelociraptorEchoAnimator;
import org.jurassicraft.client.model.animation.ZhenyuanopterusAnimator;
import org.jurassicraft.client.render.block.ActionFigureSpecialRenderer;
import org.jurassicraft.client.render.block.DNACombinatorHybridizerSpecialRenderer;
import org.jurassicraft.client.render.block.DNAExtractorSpecialRenderer;
import org.jurassicraft.client.render.block.DNASequencerSpecialRenderer;
import org.jurassicraft.client.render.block.DNASynthesizerSpecialRenderer;
import org.jurassicraft.client.render.block.EmbryoCalcificationMachineSpecialRenderer;
import org.jurassicraft.client.render.block.EmbryonicMachineSpecialRenderer;
import org.jurassicraft.client.render.block.IncubatorSpecialRenderer;
import org.jurassicraft.client.render.entity.AttractionSignRenderer;
import org.jurassicraft.client.render.entity.BluePrintRenderer;
import org.jurassicraft.client.render.entity.CageSmallRenderer;
import org.jurassicraft.client.render.entity.DinosaurEggRenderer;
import org.jurassicraft.client.render.entity.HelicopterRenderer;
import org.jurassicraft.client.render.entity.PaddockSignRenderer;
import org.jurassicraft.client.render.renderdef.IndominusRenderDef;
import org.jurassicraft.client.render.renderdef.RenderDinosaurDefinition;
import org.jurassicraft.server.api.Hybrid;
import org.jurassicraft.server.block.BlockHandler;
import org.jurassicraft.server.block.EncasedFossilBlock;
import org.jurassicraft.server.block.FossilBlock;
import org.jurassicraft.server.block.tree.AncientLeavesBlock;
import org.jurassicraft.server.block.tree.TreeType;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.base.EntityHandler;
import org.jurassicraft.server.entity.helicopter.HelicopterBaseEntity;
import org.jurassicraft.server.entity.item.AttractionSignEntity;
import org.jurassicraft.server.entity.item.BluePrintEntity;
import org.jurassicraft.server.entity.item.CageSmallEntity;
import org.jurassicraft.server.entity.item.DinosaurEggEntity;
import org.jurassicraft.server.entity.item.PaddockSignEntity;
import org.jurassicraft.server.item.DinosaurSpawnEggItem;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.item.bones.FossilItem;
import org.jurassicraft.server.plant.Plant;
import org.jurassicraft.server.plant.PlantHandler;
import org.jurassicraft.server.tile.ActionFigureTile;
import org.jurassicraft.server.tile.DNACombinatorHybridizerTile;
import org.jurassicraft.server.tile.DNAExtractorTile;
import org.jurassicraft.server.tile.DNASequencerTile;
import org.jurassicraft.server.tile.DNASynthesizerTile;
import org.jurassicraft.server.tile.EmbryoCalcificationMachineTile;
import org.jurassicraft.server.tile.EmbryonicMachineTile;
import org.jurassicraft.server.tile.IncubatorTile;

import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public enum RenderingHandler
{
    INSTANCE;

    private Map<Dinosaur, RenderDinosaurDefinition> renderDefs = Maps.newHashMap();
    private final Minecraft mc = Minecraft.getMinecraft();

    public void preInit()
    {
        for (Dinosaur dino : EntityHandler.INSTANCE.getDinosaurs())
        {
            String dinoName = dino.getName().toLowerCase().replaceAll(" ", "_");

            if (!(dino instanceof Hybrid))
            {
                for (Map.Entry<String, FossilItem> entry : ItemHandler.INSTANCE.FOSSILS.entrySet())
                {
                    List<Dinosaur> dinosaursForType = FossilItem.fossilDinosaurs.get(entry.getKey());

                    if (dinosaursForType.contains(dino))
                    {
                        ModelBakery.registerItemVariants(entry.getValue(), new ResourceLocation("jurassicraft:bones/" + dinoName + "_" + entry.getKey()));
                    }
                }
            }

            for (Map.Entry<String, FossilItem> entry : ItemHandler.INSTANCE.FRESH_FOSSILS.entrySet())
            {
                List<Dinosaur> dinosaursForType = FossilItem.fossilDinosaurs.get(entry.getKey());

                if (dinosaursForType.contains(dino))
                {
                    ModelBakery.registerItemVariants(entry.getValue(), new ResourceLocation("jurassicraft:fresh_bones/" + dinoName + "_" + entry.getKey()));
                }
            }

            ModelBakery.registerItemVariants(ItemHandler.INSTANCE.DNA, new ResourceLocation("jurassicraft:dna/dna_" + dinoName));

            if (!dino.isMammal())
            {
                ModelBakery.registerItemVariants(ItemHandler.INSTANCE.EGG, new ResourceLocation("jurassicraft:egg/egg_" + dinoName));
            }

            ModelBakery.registerItemVariants(ItemHandler.INSTANCE.DINOSAUR_MEAT, new ResourceLocation("jurassicraft:meat/meat_" + dinoName));
            ModelBakery.registerItemVariants(ItemHandler.INSTANCE.DINOSAUR_STEAK, new ResourceLocation("jurassicraft:meat/steak_" + dinoName));
            ModelBakery.registerItemVariants(ItemHandler.INSTANCE.SOFT_TISSUE, new ResourceLocation("jurassicraft:soft_tissue/soft_tissue_" + dinoName));
            ModelBakery.registerItemVariants(ItemHandler.INSTANCE.SYRINGE, new ResourceLocation("jurassicraft:syringe/syringe_" + dinoName));
            ModelBakery.registerItemVariants(ItemHandler.INSTANCE.ACTION_FIGURE, new ResourceLocation("jurassicraft:action_figure/action_figure_" + dinoName));
        }

        for (Plant plant : PlantHandler.INSTANCE.getPlants())
        {
            String name = plant.getName().toLowerCase().replaceAll(" ", "_");

            ModelBakery.registerItemVariants(ItemHandler.INSTANCE.PLANT_DNA, new ResourceLocation("jurassicraft:dna/plants/dna_" + name));
            ModelBakery.registerItemVariants(ItemHandler.INSTANCE.PLANT_SOFT_TISSUE, new ResourceLocation("jurassicraft:soft_tissue/plants/soft_tissue_" + name));
        }

        for (EnumDyeColor color : EnumDyeColor.values())
        {
            ModelBakery.registerItemVariants(Item.getItemFromBlock(BlockHandler.INSTANCE.CULTIVATOR_BOTTOM), new ResourceLocation("jurassicraft:cultivate/cultivate_bottom_" + color.getName().toLowerCase()));
        }

        for (AttractionSignEntity.AttractionSignType type : AttractionSignEntity.AttractionSignType.values())
        {
            ModelBakery.registerItemVariants(ItemHandler.INSTANCE.ATTRACTION_SIGN, new ResourceLocation("jurassicraft:attraction_sign_" + type.name().toLowerCase()));
        }

        ModelBakery.registerItemVariants(ItemHandler.INSTANCE.AMBER, new ResourceLocation("jurassicraft:amber_aphid"), new ResourceLocation("jurassicraft:amber_mosquito"));

        ModelBakery.registerItemVariants(ItemHandler.INSTANCE.CAGE_SMALL, new ResourceLocation("jurassicraft:cage_small"), new ResourceLocation("jurassicraft:cage_small_marine"));

        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.achillobator, new AchillobatorAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.anklyosaurus, new AnkylosaurusAnimator(), 0.85F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.brachiosaurus, new BrachiosaurusAnimator(), 1.5F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.carnotaurus, new CarnotaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.coelacanth, new CoelacanthAnimator(), 0.35F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.compsognathus, new CompsognathusAnimator(), 1.8F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.dilophosaurus, new DilophosaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.dunkleosteus, new DunkleosteusAnimator(), 0.35F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.gallimimus, new GallimimusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.giganotosaurus, new GiganotosaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.hypsilophodon, new HypsilophodonAnimator(), 0.65F));
        registerRenderDef(new IndominusRenderDef(0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.majungasaurus, new MajungasaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.parasaurolophus, new ParasaurolophusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.pteranodon, new PteranodonAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.rugops, new RugopsAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.segisaurus, new SegisaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.spinosaurus, new SpinosaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.stegosaurus, new StegosaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.triceratops, new TriceratopsAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.tyrannosaurus, new TyrannosaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.velociraptor, new VelociraptorAnimator(), 0.45F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.dodo, new DodoAnimator(), 0.5F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.leptictidium, new LeptictidiumAnimator(), 0.45F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.microceratus, new MicroceratusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.apatosaurus, new ApatosaurusAnimator(), 1.5F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.othnielia, new OthnieliaAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.dimorphodon, new DimorphodonAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.tylosaurus, new TylosaurusAnimator(), 0.85F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.ludodactylus, new LudodactylusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.protoceratops, new ProtoceratopsAnimator(), 0.55F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.tropeognathus, new TropeognathusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.leaellynasaura, new LeaellynasauraAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.herrerasaurus, new HerrerasaurusAnimator(), 0.75F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.velociraptor_blue, new VelociraptorBlueAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.velociraptor_charlie, new VelociraptorCharlieAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.velociraptor_delta, new VelociraptorDeltaAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.velociraptor_echo, new VelociraptorEchoAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.therizinosaurus, new TherizinosaurusAnimator(), 0.55F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.megapiranha, new MegapiranhaAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.baryonyx, new BaryonyxAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.cearadactylus, new CearadactylusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.mamenchisaurus, new MamenchisaurusAnimator(), 1.5F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.chasmosaurus, new ChasmosaurusAnimator(), 0.85F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.corythosaurus, new CorythosaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.edmontosaurus, new EdmontosaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.lambeosaurus, new LambeosaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.metriacanthosaurus, new MetriacanthosaurusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.moganopterus, new MoganopterusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.ornithomimus, new OrnithomimusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.zhenyuanopterus, new ZhenyuanopterusAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.troodon, new TroodonAnimator(), 0.65F));
        registerRenderDef(new RenderDinosaurDefinition(EntityHandler.INSTANCE.pachycephalosaurus, new PachycephalosaurusAnimator(), 0.65F));

        RenderingRegistry.registerEntityRenderingHandler(CageSmallEntity.class, new CageSmallRenderer());
        RenderingRegistry.registerEntityRenderingHandler(BluePrintEntity.class, new BluePrintRenderer());
        RenderingRegistry.registerEntityRenderingHandler(PaddockSignEntity.class, new PaddockSignRenderer());
        RenderingRegistry.registerEntityRenderingHandler(AttractionSignEntity.class, new AttractionSignRenderer());
        RenderingRegistry.registerEntityRenderingHandler(HelicopterBaseEntity.class, new HelicopterRenderer());
        RenderingRegistry.registerEntityRenderingHandler(DinosaurEggEntity.class, new DinosaurEggRenderer());
    }

    public void init()
    {
        // Blocks
        ItemModelMesher modelMesher = mc.getRenderItem().getItemModelMesher();

        int i = 0;

        for (EncasedFossilBlock fossil : BlockHandler.INSTANCE.ENCASED_FOSSILS)
        {
            this.registerBlockRenderer(modelMesher, fossil, "encased_fossil_" + i, "inventory");

            i++;
        }

        i = 0;

        for (FossilBlock fossil : BlockHandler.INSTANCE.FOSSILS)
        {
            this.registerBlockRenderer(modelMesher, fossil, "fossil_block_" + i, "inventory");

            i++;
        }

        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.PLANT_FOSSIL, "plant_fossil_block", "inventory");

        for (TreeType type : TreeType.values())
        {
            String name = type.name().toLowerCase();
            this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.ANCIENT_LEAVES.get(type), name + "_leaves", "inventory");
            this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.ANCIENT_SAPLINGS.get(type), name + "_sapling", "inventory");
            this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.ANCIENT_PLANKS.get(type), name + "_planks", "inventory");
            this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.ANCIENT_LOGS.get(type), name + "_log", "inventory");
            this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.ANCIENT_STAIRS.get(type), name + "_stairs", "inventory");
            this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.ANCIENT_SLABS.get(type), name + "_slab", "inventory");
            this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.ANCIENT_DOUBLE_SLABS.get(type), name + "_double_sab", "inventory");
            this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.PETRIFIED_LOGS.get(type), name + "_log_petrified", "inventory");
        }

        for (EnumDyeColor color : EnumDyeColor.values())
        {
            this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.CULTIVATOR_BOTTOM, color.ordinal(), "cultivate/cultivate_bottom_" + color.getName().toLowerCase(), "inventory");
        }

        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.SCALY_TREE_FERN, "scaly_tree_fern", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.SMALL_ROYAL_FERN, "small_royal_fern", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.SMALL_CHAIN_FERN, "small_chain_fern", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.SMALL_CYCAD, "small_cycad", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.CYCADEOIDEA, "bennettitalean_cycadeoidea", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.CRY_PANSY, "cry_pansy", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.ZAMITES, "cycad_zamites", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.DICKSONIA, "dicksonia", "inventory");

        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.REINFORCED_STONE, "reinforced_stone", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.REINFORCED_BRICKS, "reinforced_bricks", "inventory");

        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.CULTIVATOR_BOTTOM, "cultivate_bottom", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.CULTIVATOR_TOP, "cultivate_bottom", "inventory");

        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.AMBER_ORE, "amber_ore", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.ICE_SHARD, "ice_shard", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.CLEANING_STATION, "cleaning_station", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.FOSSIL_GRINDER, "fossil_grinder", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.DNA_SEQUENCER, "dna_sequencer", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.DNA_COMBINATOR_HYBRIDIZER, "dna_combinator_hybridizer", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.DNA_SYNTHESIZER, "dna_synthesizer", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.EMBRYONIC_MACHINE, "embryonic_machine", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.EMBRYO_CALCIFICATION_MACHINE, "embryo_calcification_machine", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.INCUBATOR, "incubator", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.DNA_EXTRACTOR, "dna_extractor", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.GYPSUM_STONE, "gypsum_stone", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.GYPSUM_COBBLESTONE, "gypsum_cobblestone", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.GYPSUM_BRICKS, "gypsum_bricks", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.ACTION_FIGURE, "action_figure_block", "inventory");

        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.MOSS, "moss", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.CLEAR_GLASS, "clear_glass", "inventory");

        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.WILD_ONION, "wild_onion_plant", "inventory");
        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.GRACILARIA, "graciliaria_seaweed", "inventory");

        BlockColors blockColors = mc.getBlockColors();
        blockColors.registerBlockColorHandler(new IBlockColor()
        {
            @Override
            public int colorMultiplier(IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex)
            {
                return BiomeColorHelper.getGrassColorAtPos(access, pos);
            }
        }, BlockHandler.INSTANCE.MOSS);

        for (Map.Entry<TreeType, AncientLeavesBlock> entry : BlockHandler.INSTANCE.ANCIENT_LEAVES.entrySet())
        {
            blockColors.registerBlockColorHandler(new IBlockColor()
            {
                @Override
                public int colorMultiplier(IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex)
                {
                    AncientLeavesBlock block = (AncientLeavesBlock) (state.getBlock());
                    return block.getTreeType() == TreeType.GINKGO ? 0xFFFFFF : BiomeColorHelper.getFoliageColorAtPos(access, pos);
                }
            }, entry.getValue());
        }

        this.registerBlockRenderer(modelMesher, BlockHandler.INSTANCE.AJUGINUCULA_SMITHII, "ajuginucula_smithii", "inventory");
    }

    public void postInit()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(DNAExtractorTile.class, new DNAExtractorSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ActionFigureTile.class, new ActionFigureSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DNASequencerTile.class, new DNASequencerSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(EmbryoCalcificationMachineTile.class, new EmbryoCalcificationMachineSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DNACombinatorHybridizerTile.class, new DNACombinatorHybridizerSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(EmbryonicMachineTile.class, new EmbryonicMachineSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DNASynthesizerTile.class, new DNASynthesizerSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(IncubatorTile.class, new IncubatorSpecialRenderer());

        RenderItem renderItem = mc.getRenderItem();
        ItemModelMesher modelMesher = renderItem.getItemModelMesher();

        // Items
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.TRACKER, "tracker", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.PLANT_CELLS_PETRI_DISH, "plant_cells_petri_dish", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.PLANT_CELLS, "plant_cells", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.PLANT_CALLUS, "plant_callus", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.NEEDLE, "needle", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.GROWTH_SERUM, "growth_serum", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.IRON_RODE, "iron_rod", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.IRON_BLADES, "iron_blades", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.CAGE_SMALL, 0, "cage_small", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.CAGE_SMALL, 1, "cage_small_marine", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.PETRI_DISH, "petri_dish", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.AMBER, "amber", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.PLASTER_AND_BANDAGE, "plaster_and_bandage", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.SPAWN_EGG, "dino_spawn_egg", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.BLUEPRINT, "blue_print", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.PADDOCK_SIGN, "paddock_sign", "inventory");

        for (AttractionSignEntity.AttractionSignType type : AttractionSignEntity.AttractionSignType.values())
        {
            this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.ATTRACTION_SIGN, type.ordinal(), "attraction_sign_" + type.name().toLowerCase(), "inventory");
        }

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.EMPTY_TEST_TUBE, "empty_test_tube", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.EMPTY_SYRINGE, "empty_syringe", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.STORAGE_DISC, "storage_disc", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.DISC_DRIVE, "disc_reader", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.LASER, "laser", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.DNA_NUCLEOTIDES, "dna_base_material", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.SEA_LAMPREY, "sea_lamprey", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.AMBER, 0, "amber_mosquito", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.AMBER, 1, "amber_aphid", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.HELICOPTER, "helicopter_spawner", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.JURASSICRAFT_THEME_DISC, "disc_jurassicraft_theme", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.DONT_MOVE_A_MUSCLE_DISC, "disc_dont_move_a_muscle", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.TROODONS_AND_RAPTORS_DISC, "disc_troodons_and_raptors", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.AMBER_KEYCHAIN, "amber_keychain", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.AMBER_CANE, "amber_cane", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.MR_DNA_KEYCHAIN, "mr_dna_keychain", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.DINO_SCANNER, "dino_scanner", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.BASIC_CIRCUIT, "basic_circuit", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.ADVANCED_CIRCUIT, "advanced_circuit", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.IRON_NUGGET, "iron_nugget", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.GYPSUM_POWDER, "gypsum_powder", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.AJUGINUCULA_SMITHII_SEEDS, "ajuginucula_smithii_seeds", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.AJUGINUCULA_SMITHII_LEAVES, "ajuginucula_smithii_leaves", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.AJUGINUCULA_SMITHII_OIL, "ajuginucula_smithii_oil", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.WILD_ONION, "wild_onion", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.GRACILARIA, "gracilaria", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.LIQUID_AGAR, "liquid_agar", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.PLANT_FOSSIL, "plant_fossil", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.TWIG_FOSSIL, "twig_fossil", "inventory");

        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.KEYBOARD, "keyboard", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.COMPUTER_SCREEN, "computer_screen", "inventory");
        this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.DNA_ANALYZER, "dna_analyzer", "inventory");

        int meta = 0;

        for (Dinosaur dino : EntityHandler.INSTANCE.getDinosaurs())
        {
            String dinoName = dino.getName().toLowerCase().replaceAll(" ", "_");

            for (Map.Entry<String, FossilItem> entry : ItemHandler.INSTANCE.FOSSILS.entrySet())
            {
                List<Dinosaur> dinosaursForType = FossilItem.fossilDinosaurs.get(entry.getKey());

                if (dinosaursForType.contains(dino))
                {
                    this.registerItemRenderer(modelMesher, entry.getValue(), meta, "bones/" + dinoName + "_" + entry.getKey(), "inventory");
                }
            }

            for (Map.Entry<String, FossilItem> entry : ItemHandler.INSTANCE.FRESH_FOSSILS.entrySet())
            {
                List<Dinosaur> dinosaursForType = FossilItem.fossilDinosaurs.get(entry.getKey());

                if (dinosaursForType.contains(dino))
                {
                    this.registerItemRenderer(modelMesher, entry.getValue(), meta, "fresh_bones/" + dinoName + "_" + entry.getKey(), "inventory");
                }
            }

            this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.DNA, meta, "dna/dna_" + dinoName, "inventory");
            this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.EGG, meta, "egg/egg_" + dinoName, "inventory");
            this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.DINOSAUR_MEAT, meta, "meat/meat_" + dinoName, "inventory");
            this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.DINOSAUR_STEAK, meta, "meat/steak_" + dinoName, "inventory");
            this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.SOFT_TISSUE, meta, "soft_tissue/soft_tissue_" + dinoName, "inventory");
            this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.SYRINGE, meta, "syringe/syringe_" + dinoName, "inventory");
            this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.ACTION_FIGURE, meta, "action_figure/action_figure_" + dinoName, "inventory");

            meta++;
        }

        meta = 0;

        for (Plant plant : PlantHandler.INSTANCE.getPlants())
        {
            String name = plant.getName().toLowerCase().replaceAll(" ", "_");

            this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.PLANT_DNA, meta, "dna/plants/dna_" + name, "inventory");
            this.registerItemRenderer(modelMesher, ItemHandler.INSTANCE.PLANT_SOFT_TISSUE, meta, "soft_tissue/plants/soft_tissue_" + name, "inventory");

            meta++;
        }

        ItemColors itemColors = mc.getItemColors();
        itemColors.registerItemColorHandler(new IItemColor()
        {
            @Override
            public int getColorFromItemstack(ItemStack stack, int tintIndex)
            {
                DinosaurSpawnEggItem item = (DinosaurSpawnEggItem) stack.getItem();
                Dinosaur dino = item.getDinosaur(stack);

                if (dino != null)
                {
                    int mode = item.getMode(stack);

                    if (mode == 0)
                    {
                        mode = JurassiCraft.timerTicks % 64 > 32 ? 1 : 2;
                    }

                    if (mode == 1)
                    {
                        return tintIndex == 0 ? dino.getEggPrimaryColorMale() : dino.getEggSecondaryColorMale();
                    }
                    else
                    {
                        return tintIndex == 0 ? dino.getEggPrimaryColorFemale() : dino.getEggSecondaryColorFemale();
                    }
                }

                return 0xFFFFFF;
            }
        }, ItemHandler.INSTANCE.SPAWN_EGG);
    }

    /**
     * Registers an item renderer
     */
    public void registerItemRenderer(ItemModelMesher itemModelMesher, Item item, final String path, final String type)
    {
        itemModelMesher.register(item, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation(JurassiCraft.MODID + ":" + path, type);
            }
        });
    }

    /**
     * Registers an item renderer with metadata
     */
    public void registerItemRenderer(ItemModelMesher itemModelMesher, Item item, int meta, String path, String type)
    {
        itemModelMesher.register(item, meta, new ModelResourceLocation(JurassiCraft.MODID + ":" + path, type));
    }

    /**
     * Registers an block renderer with metadata
     */
    public void registerBlockRenderer(ItemModelMesher itemModelMesher, Block block, int meta, String path, String type)
    {
        itemModelMesher.register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(JurassiCraft.MODID + ":" + path, type));
    }

    /**
     * Registers a block renderer
     */
    public void registerBlockRenderer(ItemModelMesher itemModelMesher, Block block, final String path, final String type)
    {
        itemModelMesher.register(Item.getItemFromBlock(block), new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation(JurassiCraft.MODID + ":" + path, type);
            }
        });
    }

    private void registerRenderDef(RenderDinosaurDefinition renderDef)
    {
        renderDefs.put(renderDef.getDinosaur(), renderDef);
        RenderingRegistry.registerEntityRenderingHandler(renderDef.getDinosaur().getDinosaurClass(), renderDef);
    }

    public RenderDinosaurDefinition getRenderDef(Dinosaur dino)
    {
        return renderDefs.get(dino);
    }
}
