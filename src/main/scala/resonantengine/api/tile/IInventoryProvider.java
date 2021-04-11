package resonantengine.api.tile;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import resonantengine.api.graph.node.IExternalInventory;

public interface IInventoryProvider {
   IExternalInventory getInventory();

   boolean canStore(ItemStack var1, int var2, ForgeDirection var3);

   boolean canRemove(ItemStack var1, int var2, ForgeDirection var3);
}
