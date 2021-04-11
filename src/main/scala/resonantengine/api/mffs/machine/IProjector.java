package resonantengine.api.mffs.machine;

import net.minecraft.inventory.IInventory;
import resonantengine.api.tile.IBlockFrequency;

import java.util.Set;

public interface IProjector extends IInventory, IFieldMatrix, IBlockFrequency {
   void projectField();

   void destroyField();

   int getProjectionSpeed();

   long getTicks();

   Set getForceFields();
}
