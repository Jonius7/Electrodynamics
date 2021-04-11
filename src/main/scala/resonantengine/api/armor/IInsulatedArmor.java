package resonantengine.api.armor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IInsulatedArmor extends IArmorSet {
   float onElectricalDamage(ItemStack var1, EntityLivingBase var2, Object var3, long var4, float var6);
}
