package edx.electrical;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edx.core.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class Models {

    public static IModelCustom motor = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "motor.tcn"));
    public static IModelCustom laseremitter = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "laserEmitter.tcn"));
    public static IModelCustom laserreceiver = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "laserReceiver.tcn"));
    public static IModelCustom battery = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "battery/battery.tcn"));
    public static IModelCustom mirror = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "mirror.tcn"));
    public static IModelCustom focuscrystal = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "focusCrystal.tcn"));

}
