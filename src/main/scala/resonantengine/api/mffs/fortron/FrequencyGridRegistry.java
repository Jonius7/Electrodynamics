package resonantengine.api.mffs.fortron;

import net.minecraft.world.World;
import resonantengine.api.tile.IBlockFrequency;
import resonantengine.lib.transform.region.Cuboid;
import resonantengine.lib.transform.vector.Vector3;

import java.util.Set;

public class FrequencyGridRegistry {
   public static FrequencyGridRegistry.IFrequencyGrid CLIENT_INSTANCE;
   public static FrequencyGridRegistry.IFrequencyGrid SERVER_INSTANCE;

   public static FrequencyGridRegistry.IFrequencyGrid instance() {
      Thread thr = Thread.currentThread();
      return !thr.getName().equals("Server thread") && !(thr instanceof IServerThread) ? CLIENT_INSTANCE : SERVER_INSTANCE;
   }

   public interface IFrequencyGrid {
      void add(IBlockFrequency var1);

      void remove(IBlockFrequency var1);

      Set getNodes();

      Set getNodes(Class var1);

      Set getNodes(int var1);

      Set getNodes(Class var1, int var2);

      Set getNodes(World var1, Vector3 var2, int var3, int var4);

      Set getNodes(Class var1, World var2, Vector3 var3, int var4, int var5);

      Set getNodes(World var1, Cuboid var2, int var3);

      Set getNodes(Class var1, World var2, Cuboid var3, int var4);
   }
}
