package resonantengine.api.mffs.card;

import net.minecraft.item.ItemStack;
import resonantengine.lib.access.AbstractAccess;

public interface IAccessCard extends ICard {
   AbstractAccess getAccess(ItemStack var1);

   void setAccess(ItemStack var1, AbstractAccess var2);
}
