package resonantengine.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

public interface IRotatable {
   void setDirection(ForgeDirection var1);

   ForgeDirection getDirection();
}
