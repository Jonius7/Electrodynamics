package edx.core.ftbcompat;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class FTBCompatOff implements IFTBCompat {
    @Override
    public boolean canDestroyBlock(Vec3 blockpos, Vec3 laserpos, World world) {
        return true;
    }
}
