package resonantengine.api.item;

import net.minecraft.item.ItemStack;

public interface IEnergyItem {
   double recharge(ItemStack var1, double var2, boolean var4);

   double discharge(ItemStack var1, double var2, boolean var4);

   double getEnergy(ItemStack var1);

   double getEnergyCapacity(ItemStack var1);

   ItemStack setEnergy(ItemStack var1, double var2);
}
