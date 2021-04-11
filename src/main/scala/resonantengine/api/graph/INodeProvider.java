package resonantengine.api.graph;

import net.minecraftforge.common.util.ForgeDirection;
import resonantengine.api.graph.node.INode;

public interface INodeProvider {
   <N extends INode> N getNode(Class<? extends N> var1, ForgeDirection var2);
}
