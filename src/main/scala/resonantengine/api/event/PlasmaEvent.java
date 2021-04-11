package resonantengine.api.event;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public abstract class PlasmaEvent extends WorldEvent {
   public PlasmaEvent(World world) {
      super(world);
   }

   public static class SpawnPlasmaEvent extends PlasmaEvent {
      public final int x;
      public final int y;
      public final int z;
      public final int temperature;

      public SpawnPlasmaEvent(World world, int x, int y, int z, int temperature) {
         super(world);
         this.x = x;
         this.y = y;
         this.z = z;
         this.temperature = temperature;
      }
   }
}
