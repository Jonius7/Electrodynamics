package edx.mechanical;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edx.core.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class Models {

    public static IModelCustom pump = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "pump.tcn"));
    public static IModelCustom grinder = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "grinder.obj"));
    public static IModelCustom mixer = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "mixer.tcn"));
    public static IModelCustom wind = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "windTurbines.obj"));

}
