package resonantengine.api.mffs.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public abstract class EventForceMobilize extends WorldEvent {
   public int beforeX;
   public int beforeY;
   public int beforeZ;
   public int afterX;
   public int afterY;
   public int afterZ;

   public EventForceMobilize(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
      super(world);
      this.beforeX = beforeX;
      this.beforeY = beforeY;
      this.beforeZ = beforeZ;
      this.afterX = afterX;
      this.afterY = afterY;
      this.afterZ = afterZ;
   }

   public static class EventPostForceManipulate extends EventForceMobilize {
      public EventPostForceManipulate(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
         super(world, beforeX, beforeY, beforeZ, afterX, afterY, afterZ);
      }
   }

   @Cancelable
   public static class EventPreForceManipulate extends EventForceMobilize {
      public EventPreForceManipulate(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
         super(world, beforeX, beforeY, beforeZ, afterX, afterY, afterZ);
      }
   }

   @Cancelable
   public static class EventCheckForceManipulate extends EventForceMobilize {
      public EventCheckForceManipulate(World world, int beforeX, int beforeY, int beforeZ, int afterX, int afterY, int afterZ) {
         super(world, beforeX, beforeY, beforeZ, afterX, afterY, afterZ);
      }
   }
}
