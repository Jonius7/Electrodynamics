package resonantengine.api.mffs.modules;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import resonantengine.api.mffs.machine.IFieldMatrix;
import resonantengine.api.mffs.machine.IProjector;
import resonantengine.lib.transform.vector.Vector3;

import java.util.Set;

public interface IModule extends IFortronCost {
   boolean onProject(IProjector var1, Set var2);

   boolean onDestroy(IProjector var1, Set var2);

   int onProject(IProjector var1, Vector3 var2);

   boolean onCollideWithForceField(World var1, int var2, int var3, int var4, Entity var5, ItemStack var6);

   void onPreCalculate(IFieldMatrix var1, Set var2);

   void onPostCalculate(IFieldMatrix var1, Set var2);

   boolean requireTicks(ItemStack var1);
}
