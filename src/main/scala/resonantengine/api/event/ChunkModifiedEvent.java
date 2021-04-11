package resonantengine.api.event;

import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;

public abstract class ChunkModifiedEvent extends ChunkEvent {
   public ChunkModifiedEvent(Chunk chunk) {
      super(chunk);
   }

   public static class ChunkSetBlockEvent extends ChunkModifiedEvent {
      public final int x;
      public final int y;
      public final int z;
      public final int blockMetadata;
      public final Block block;

      public ChunkSetBlockEvent(Chunk chunk, int chunkX, int y, int chunkZ, Block block, int blockMetadata) {
         super(chunk);
         this.x = (chunk.xPosition << 4) + chunkX;
         this.y = y;
         this.z = (chunk.zPosition << 4) + chunkZ;
         this.block = block;
         this.blockMetadata = blockMetadata;
      }
   }
}
