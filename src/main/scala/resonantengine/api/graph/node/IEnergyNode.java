package resonantengine.api.graph.node;

import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyNode extends INode {
   double addEnergy(ForgeDirection var1, double var2, boolean var4);

   double removeEnergy(ForgeDirection var1, double var2, boolean var4);

   double getEnergy(ForgeDirection var1);

   double getEnergyCapacity(ForgeDirection var1);
}
