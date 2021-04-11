package edx.core.ftbcompat;

import ftb.utils.world.LMWorldServer;
import ftb.utils.world.claims.ClaimedChunk;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class FTBCompatOn implements IFTBCompat {
    @Override
    public boolean canDestroyBlock(Vec3 blockpos, Vec3 laserpos, World world) {
        int bcx = ((int) blockpos.xCoord) >> 4;
        int bcz = ((int) blockpos.zCoord) >> 4;
        int lcx = ((int) laserpos.xCoord) >> 4;
        int lcz = ((int) laserpos.zCoord) >> 4;

        ClaimedChunk bchunk = LMWorldServer.inst.claimedChunks.getChunk(world.provider.dimensionId, bcx, bcz);
        ClaimedChunk lchunk = LMWorldServer.inst.claimedChunks.getChunk(world.provider.dimensionId, lcx, lcz);

        if (bchunk == null) {
            return true;
        } else if (lchunk == null) {
            return false;
        } else {
            return bchunk.ownerID == lchunk.ownerID;
        }
    }
}
