package resonantengine.api.edx.machine;

import net.minecraft.item.ItemStack;

public interface IReactorComponent {
   void onReact(ItemStack var1, IReactor var2);
}
