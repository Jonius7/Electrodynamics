package resonantengine.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

import java.util.Set;

public interface IIO {
   Set getInputDirections();

   Set getOutputDirections();

   void setIO(ForgeDirection var1, int var2);

   int getIO(ForgeDirection var1);
}
