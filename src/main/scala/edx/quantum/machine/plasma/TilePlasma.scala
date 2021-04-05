package edx.quantum.machine.plasma

import java.util.ArrayList

import net.minecraft.block.material.Material
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.DamageSource
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.ForgeDirection
import resonantengine.api.event.PlasmaEvent
import resonantengine.lib.grid.thermal.GridThermal
import resonantengine.lib.mod.config.Config
import resonantengine.lib.modcontent.block.ResonantTile
import resonantengine.lib.transform.vector.Vector3

object TilePlasma
{
  @Config var plasmaMaxTemperature: Int = 1000000
}

class TilePlasma extends ResonantTile(Material.lava)
{
  private var temperature: Double = TilePlasma.plasmaMaxTemperature

  //Constructor
  textureName = "plasma"
  isOpaqueCube = false

  override def getLightValue(access: IBlockAccess): Int =
  {
    return 7
  }

  override def isSolid(access: IBlockAccess, side: Int): Boolean =
  {
    return false
  }

  override def getDrops(metadata: Int, fortune: Int): ArrayList[ItemStack] =
  {
    return new ArrayList[ItemStack]
  }

  override def getRenderBlockPass: Int =
  {
    return 1
  }

  override def collide(entity: Entity)
  {
    entity.attackEntityFrom(DamageSource.inFire, 100)
  }

  override def update
  {
    super.update
    GridThermal.addHeat(toVectorWorld, ((temperature - GridThermal.getTemperature(toVectorWorld)) * 0.1f).asInstanceOf[Float])
    if (ticks % 20 == 0)
    {
      temperature /= 1.5
      if (temperature <= TilePlasma.plasmaMaxTemperature / 10)
      {
        worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.fire, 0, 3)
        return
      }
      for (i <- 0 to 6)
      {
        if (worldObj.rand.nextFloat < 0.4)
        {
          val diDian: Vector3 = toVectorWorld
          diDian.add(ForgeDirection.getOrientation(i))
          val tileEntity: TileEntity = diDian.getTileEntity(worldObj)
          if (!(tileEntity.isInstanceOf[TilePlasma]))
          {
            MinecraftForge.EVENT_BUS.post(new PlasmaEvent.SpawnPlasmaEvent(worldObj, diDian.xi, diDian.yi, diDian.zi, temperature.asInstanceOf[Int]))
          }
        }
      }
    }
  }

  def setTemperature(newTemperature: Int)
  {
    temperature = newTemperature
  }

}
