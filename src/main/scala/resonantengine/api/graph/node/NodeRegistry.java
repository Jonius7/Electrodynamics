package resonantengine.api.graph.node;

import resonantengine.api.graph.INodeProvider;

import java.util.HashMap;

public class NodeRegistry {
   private static final HashMap INTERFACE_NODE_MAP = new HashMap();

   public static void register(Class nodeInterface, Class nodeClass) {
      INTERFACE_NODE_MAP.put(nodeInterface, nodeClass);
   }

   public static INode get(INodeProvider parent, Class nodeInterface) {
      Class nodeClass = (Class)INTERFACE_NODE_MAP.get(nodeInterface);

      try {
         return (INode)nodeClass.getConstructor(INodeProvider.class).newInstance(parent);
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }
}
