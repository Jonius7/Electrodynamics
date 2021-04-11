package resonantengine.api.armor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public interface IArmorSet {
   int getArmorType();

   boolean isPartOfSet(ItemStack var1, ItemStack var2);

   boolean areAllPartsNeeded(ItemStack var1, EntityLivingBase var2, DamageSource var3, Object... var4);
}
