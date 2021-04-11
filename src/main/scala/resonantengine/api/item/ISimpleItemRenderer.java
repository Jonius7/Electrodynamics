package resonantengine.api.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

public interface ISimpleItemRenderer {
   void renderInventoryItem(ItemRenderType var1, ItemStack var2, Object... var3);
}
