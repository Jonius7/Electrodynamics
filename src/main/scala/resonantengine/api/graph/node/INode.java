package resonantengine.api.graph.node;

import resonantengine.api.graph.INodeProvider;

public interface INode {
   void reconstruct();

   void deconstruct();

   INodeProvider getParent();
}
