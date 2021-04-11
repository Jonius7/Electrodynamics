package resonantengine.api.graph;

import java.util.Set;

public interface IGraph {
   Set getNodes();

   void add(Object var1);

   void remove(Object var1);

   void reconstruct();

   void deconstruct();
}
