package resonantengine.api.edx.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Iterator;

public abstract class RecipeResource {
   public final boolean hasChance;
   public final float chance;

   protected RecipeResource() {
      this.hasChance = false;
      this.chance = 100.0F;
   }

   protected RecipeResource(float chance) {
      this.hasChance = true;
      this.chance = chance;
   }

   public boolean hasChance() {
      return this.hasChance;
   }

   public float getChance() {
      return this.chance;
   }

   public abstract ItemStack getItemStack();

   public static class FluidStackResource extends RecipeResource {
      public final FluidStack fluidStack;

      public FluidStackResource(FluidStack fs) {
         this.fluidStack = fs;
      }

      public FluidStackResource(FluidStack fs, float chance) {
         super(chance);
         this.fluidStack = fs;
      }

      public boolean equals(Object obj) {
         if (obj instanceof RecipeResource.FluidStackResource) {
            return this.equals(((RecipeResource.FluidStackResource)obj).fluidStack);
         } else {
            return obj instanceof FluidStack ? ((FluidStack)obj).equals(this.fluidStack) : false;
         }
      }

      public ItemStack getItemStack() {
         return null;
      }

      public String toString() {
         return "[FluidStackResource: " + this.fluidStack.getFluid().getName() + "]";
      }
   }

   public static class OreDictResource extends RecipeResource {
      public final String name;

      public OreDictResource(String s) {
         this.name = s;
         if (OreDictionary.getOres(this.name).size() <= 0) {
            throw new RuntimeException("Added invalid OreDictResource recipe: " + this.name);
         }
      }

      public OreDictResource(String s, float chance) {
         super(chance);
         this.name = s;
      }

      public boolean equals(Object obj) {
         if (obj instanceof RecipeResource.OreDictResource) {
            return this.name.equals(((RecipeResource.OreDictResource)obj).name);
         } else if (obj instanceof RecipeResource.ItemStackResource) {
            return this.equals(((RecipeResource.ItemStackResource)obj).itemStack);
         } else {
            if (obj instanceof ItemStack) {
               Iterator i$ = OreDictionary.getOres(this.name).iterator();

               while(i$.hasNext()) {
                  ItemStack is = (ItemStack)i$.next();
                  if (is.isItemEqual((ItemStack)obj)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public ItemStack getItemStack() {
         return ((ItemStack)OreDictionary.getOres(this.name).get(0)).copy();
      }

      public String toString() {
         return "[OreDictResource: " + this.name + "]";
      }
   }

   public static class ItemStackResource extends RecipeResource {
      public final ItemStack itemStack;

      public ItemStackResource(ItemStack is) {
         this.itemStack = is;
      }

      public ItemStackResource(ItemStack is, float chance) {
         super(chance);
         this.itemStack = is;
      }

      public boolean equals(Object obj) {
         if (obj instanceof RecipeResource.ItemStackResource) {
            return this.itemStack.isItemEqual(((RecipeResource.ItemStackResource)obj).itemStack);
         } else {
            return obj instanceof ItemStack ? this.itemStack.isItemEqual((ItemStack)obj) : false;
         }
      }

      public ItemStack getItemStack() {
         return this.itemStack.copy();
      }

      public String toString() {
         return "[ItemStackResource: " + this.itemStack.toString() + "]";
      }
   }
}
