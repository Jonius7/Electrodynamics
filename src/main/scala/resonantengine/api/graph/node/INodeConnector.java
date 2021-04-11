package resonantengine.api.graph.node;

import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;
import java.util.Set;

/** @deprecated */
@Deprecated
public interface INodeConnector extends INode {
   boolean canConnect(Object var1, ForgeDirection var2);

   /** @deprecated */
   @Deprecated
   Set connections();

   Map directionMap();
}
