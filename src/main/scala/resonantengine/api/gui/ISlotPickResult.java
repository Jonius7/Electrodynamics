package resonantengine.api.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ISlotPickResult {
   void onPickUpFromSlot(EntityPlayer var1, int var2, ItemStack var3);
}
