package resonantengine.api.armor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IAntiPoisonArmor extends IArmorSet {
   boolean isProtectedFromPoison(ItemStack var1, EntityLivingBase var2, String var3);

   void onProtectFromPoison(ItemStack var1, EntityLivingBase var2, String var3);
}
