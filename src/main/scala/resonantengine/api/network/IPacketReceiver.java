package resonantengine.api.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import resonantengine.core.network.discriminator.PacketType;

public interface IPacketReceiver {
   void read(ByteBuf var1, EntityPlayer var2, PacketType var3);
}
