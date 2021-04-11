package resonantengine.api.mffs.modules;

import net.minecraft.item.ItemStack;

import java.util.Set;

public interface IModuleProvider {
   ItemStack getModule(IModule var1);

   int getModuleCount(IModule var1, int... var2);

   Set getModuleStacks(int... var1);

   Set getModules(int... var1);

   int getFortronCost();
}
