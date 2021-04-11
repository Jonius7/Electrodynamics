package resonantengine.api.misc;

import net.minecraft.nbt.NBTTagCompound;

public interface ISave {
   void save(NBTTagCompound var1);

   void load(NBTTagCompound var1);
}
