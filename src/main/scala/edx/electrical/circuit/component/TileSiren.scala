package edx.electrical.circuit.component

import edx.core.Reference
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.util.ForgeDirection
import resonantengine.lib.grid.energy.electric.NodeElectricComponent
import resonantengine.lib.modcontent.block.ResonantTile
import resonantengine.lib.transform.vector.Vector3
import resonantengine.prefab.block.impl.TBlockNodeProvider

import scala.collection.convert.wrapAll._

/**
 * Siren block
 */
class TileSiren extends ResonantTile(Material.wood) with TBlockNodeProvider
{
  val electricNode = new NodeElectricComponent(this)
  nodes.add(electricNode)
  electricNode.dynamicTerminals = true
  electricNode.setPositives(Set(ForgeDirection.NORTH, ForgeDirection.UP, ForgeDirection.EAST))
  electricNode.setNegatives(Set(ForgeDirection.SOUTH, ForgeDirection.DOWN, ForgeDirection.WEST))

  override def update()
  {
    super.update()

    if (ticks % 30 == 0)
    {
      if (world != null)
      {
        val metadata: Int = world.getBlockMetadata(x.toInt, y.toInt, z.toInt)
        if (world.getBlockPowerInput(x.toInt, y.toInt, z.toInt) > 0)
        {
          var volume: Float = 0.5f
          for (i <- 0 to 6)
          {
            val check: Vector3 = toVectorWorld.add(ForgeDirection.getOrientation(i))
            if (check.getBlock(world) == getBlockType)
            {
              volume *= 1.5f
            }
          }
          world.playSoundEffect(x, y, z, Reference.prefix + "siren", volume, 1f - 0.18f * (metadata / 15f))
        }
      }

      if (!world.isRemote)
      {
        val volume = electricNode.power.toFloat / 1000f
        world.playSoundEffect(x, y, z, Reference.prefix + "siren", volume, 1f - 0.18f * (metadata / 15f))
      }
    }
  }

  override def configure(player: EntityPlayer, side: Int, hit: Vector3): Boolean =
  {
    var metadata: Int = world.getBlockMetadata(x.toInt, y.toInt, z.toInt)
    if (player.isSneaking)
    {
      metadata -= 1
    }
    else
    {
      metadata += 1
    }
    metadata = Math.max(metadata % 16, 0)
    world.setBlockMetadataWithNotify(x.toInt, y.toInt, z.toInt, metadata, 2)
    return true
  }
}
