package resonantengine.api.edx.recipe;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QuantumAssemblerRecipes {
   public static final List RECIPES = new ArrayList();

   public static boolean hasItemStack(ItemStack itemStack) {
      Iterator i$ = RECIPES.iterator();

      ItemStack output;
      do {
         if (!i$.hasNext()) {
            return false;
         }

         output = (ItemStack)i$.next();
      } while(!output.isItemEqual(itemStack));

      return true;
   }

   public static void addRecipe(ItemStack itemStack) {
      if (itemStack != null && itemStack.isStackable()) {
         RECIPES.add(itemStack);
      }

   }
}
