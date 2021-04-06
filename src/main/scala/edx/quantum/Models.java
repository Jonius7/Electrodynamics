package edx.quantum;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edx.core.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class Models {

    public static IModelCustom cellTop = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "reactorCellTop.tcn"));
    public static IModelCustom cellMiddle = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "reactorCellMiddle.tcn"));
    public static IModelCustom cellBottom = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "reactorCellBottom.tcn"));

}
