package resonantengine.api.edx.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.Map.Entry;

public final class MachineRecipes {
   public static MachineRecipes instance = new MachineRecipes();
   private final Map recipes = new HashMap();

   public RecipeResource getResourceFromObject(Object obj) {
      if (obj instanceof String) {
         return new RecipeResource.OreDictResource((String)obj);
      } else if (obj instanceof Block) {
         return new RecipeResource.ItemStackResource(new ItemStack((Block)obj));
      } else if (obj instanceof Item) {
         return new RecipeResource.ItemStackResource(new ItemStack((Item)obj));
      } else if (obj instanceof ItemStack) {
         return new RecipeResource.ItemStackResource((ItemStack)obj);
      } else if (obj instanceof FluidStack) {
         return new RecipeResource.FluidStackResource((FluidStack)obj);
      } else {
         return obj instanceof RecipeResource ? (RecipeResource)obj : null;
      }
   }

   public void addRecipe(String machine, Object inputObj, Object... outputObj) {
      this.addRecipe(machine, new Object[]{inputObj}, outputObj);
   }

   public void addRecipe(String machine, Object[] inputObj, Object[] outputObj) {
      RecipeResource[] inputs = new RecipeResource[inputObj.length];

      for(int i = 0; i < inputs.length; ++i) {
         RecipeResource input = this.getResourceFromObject(inputObj[i]);
         if (input == null) {
            throw new RuntimeException("Tried to add invalid " + machine + " recipe input: " + inputObj[i]);
         }

         inputs[i] = input;
      }

      RecipeResource[] outputs = new RecipeResource[outputObj.length];

      for(int i = 0; i < outputs.length; ++i) {
         RecipeResource output = this.getResourceFromObject(outputObj[i]);
         if (output == null) {
            throw new RuntimeException("Tried to add invalid " + machine + " recipe output: " + outputObj[i]);
         }

         outputs[i] = output;
      }

      this.addRecipe(machine, inputs, outputs);
   }

   public void addRecipe(String machine, RecipeResource[] input, RecipeResource[] output) {
      this.getRecipes(machine).put(input, output);
   }

   public void removeRecipe(String machine, RecipeResource[] input) {
      this.getRecipes(machine).remove(input);
   }

   public Map getRecipes(String machine) {
      machine = machine.toLowerCase(Locale.ENGLISH);
      if (!this.recipes.containsKey(machine)) {
         this.recipes.put(machine, new HashMap());
      }

      return (Map)this.recipes.get(machine);
   }

   public RecipeResource[] getOutput(String machine, RecipeResource... input) {
      Iterator it = this.getRecipes(machine).entrySet().iterator();

      Entry entry;
      RecipeResource[] compare;
      RecipeResource[] copyA;
      RecipeResource[] copyB;
      do {
         if (!it.hasNext()) {
            return new RecipeResource[0];
         }

         entry = (Entry)it.next();
         compare = (RecipeResource[])entry.getKey();
         copyA = (RecipeResource[])Arrays.copyOf(input, input.length);
         copyB = (RecipeResource[])Arrays.copyOf(compare, compare.length);
         Arrays.sort(copyA);
         Arrays.sort(copyB);
      } while(!Arrays.equals(compare, input) && !Arrays.equals(copyA, copyB));

      return (RecipeResource[])entry.getValue();
   }

   public RecipeResource[] getOutput(String machine, Object... inputs) {
      RecipeResource[] resourceInputs = new RecipeResource[inputs.length];

      for(int i = 0; i < inputs.length; ++i) {
         resourceInputs[i] = this.getResourceFromObject(inputs[i]);
      }

      return this.getOutput(machine, resourceInputs);
   }
}
