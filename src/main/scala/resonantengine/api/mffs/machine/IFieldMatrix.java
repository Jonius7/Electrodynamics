package resonantengine.api.mffs.machine;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import resonantengine.api.mffs.modules.IModule;
import resonantengine.api.mffs.modules.IModuleProvider;
import resonantengine.api.mffs.modules.IProjectorMode;
import resonantengine.lib.transform.vector.Vector3;

import java.util.Set;

public interface IFieldMatrix extends IModuleProvider, IActivatable, IPermissionProvider {
   IProjectorMode getMode();

   ItemStack getModeStack();

   int[] getDirectionSlots(ForgeDirection var1);

   int[] getModuleSlots();

   int getSidedModuleCount(IModule var1, ForgeDirection... var2);

   Vector3 getTranslation();

   Vector3 getPositiveScale();

   Vector3 getNegativeScale();

   int getRotationYaw();

   int getRotationPitch();

   Set getCalculatedField();

   Set getInteriorPoints();

   ForgeDirection getDirection();
}
