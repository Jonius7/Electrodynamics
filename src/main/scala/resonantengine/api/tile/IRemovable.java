package resonantengine.api.tile;

import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public interface IRemovable {
   List getRemovedItems(EntityPlayer var1);

   public interface ICustomRemoval extends IRemovable {
      boolean canBeRemoved(EntityPlayer var1);
   }

   public interface IPickup extends IRemovable {
   }

   public interface ISneakPickup extends IRemovable {
   }

   public interface IWrenchable extends IRemovable {
   }

   public interface ISneakWrenchable extends IRemovable {
   }
}
