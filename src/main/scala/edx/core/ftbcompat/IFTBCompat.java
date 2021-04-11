package edx.core.ftbcompat;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface IFTBCompat {

    boolean canDestroyBlock(Vec3 blockpos, Vec3 laserpos, World world);

}
