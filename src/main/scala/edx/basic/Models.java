package edx.basic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edx.core.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class Models {

    public static IModelCustom gutter = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "gutter.tcn"));
    public static IModelCustom[] workbench = new IModelCustom[] {AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "workbench_0.obj")), AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "workbench_1.obj"))};
    public static IModelCustom glassjar = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "glassJar.tcn"));
    public static IModelCustom sieve = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "sieve.tcn"));
}
