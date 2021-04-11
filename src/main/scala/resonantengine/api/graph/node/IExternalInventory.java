package resonantengine.api.graph.node;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import resonantengine.api.misc.ISave;

public interface IExternalInventory extends ISidedInventory, ISave {
   ItemStack[] getContainedItems();

   void clear();
}
