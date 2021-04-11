package resonantengine.api.mffs.card;

import net.minecraft.item.ItemStack;
import resonantengine.lib.transform.vector.VectorWorld;

public interface ICoordLink {
   void setLink(ItemStack var1, VectorWorld var2);

   VectorWorld getLink(ItemStack var1);
}
