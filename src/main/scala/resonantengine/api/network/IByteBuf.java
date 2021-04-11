package resonantengine.api.network;

import io.netty.buffer.ByteBuf;

public interface IByteBuf {
   void writeBytes(ByteBuf var1);

   void readBytes(ByteBuf var1);
}
