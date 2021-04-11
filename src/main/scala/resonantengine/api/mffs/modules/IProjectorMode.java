package resonantengine.api.mffs.modules;

import resonantengine.api.mffs.machine.IFieldMatrix;
import resonantengine.api.mffs.machine.IProjector;
import resonantengine.lib.transform.vector.Vector3;

import java.util.Set;

public interface IProjectorMode extends IFortronCost {
   Set getExteriorPoints(IFieldMatrix var1);

   Set getInteriorPoints(IFieldMatrix var1);

   boolean isInField(IFieldMatrix var1, Vector3 var2);

   void render(IProjector var1, double var2, double var4, double var6, float var8, long var9);
}
