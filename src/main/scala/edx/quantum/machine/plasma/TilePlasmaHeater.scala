package edx.quantum.machine.plasma

import java.util.HashMap

import cpw.mods.fml.common.network.ByteBufUtils
import edx.quantum.QuantumContent
import io.netty.buffer.ByteBuf
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.Packet
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids._
import resonantengine.api.network.IPacketReceiver
import resonantengine.core.ResonantEngine
import resonantengine.core.network.discriminator.{PacketTile, PacketType}
import resonantengine.lib.grid.energy.EnergyStorage
import resonantengine.lib.grid.energy.electric.NodeElectricComponent
import resonantengine.lib.mod.config.Config
import resonantengine.lib.modcontent.block.ResonantTile
import resonantengine.lib.transform.vector.Vector3
import resonantengine.lib.utility.science.UnitDisplay
import resonantengine.lib.utility.{FluidUtility, LanguageUtility}
import resonantengine.prefab.block.impl.{TBlockNodeProvider, TEnergyProvider}

object TilePlasmaHeater
{
  var power: Long = 10000000000L
  @Config var plasmaHeatAmount: Int = 100
}

class TilePlasmaHeater extends ResonantTile(Material.iron) with TBlockNodeProvider with IPacketReceiver with IFluidHandler with TEnergyProvider
{
  final val tankInputDeuterium: FluidTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10)
  final val tankInputTritium: FluidTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10)
  final val tankOutput: FluidTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10)
  private val electricNode = new NodeElectricComponent(this)
  var rotation: Float = 0

  //Constructor

  //TODO: Dummy
  energy = new EnergyStorage
  energy.max = TilePlasmaHeater.power
  normalRender = false
  isOpaqueCube = false

  override def update()
  {
    super.update()
    rotation = (rotation + energy.value / 10000f).asInstanceOf[Float]
    if (!worldObj.isRemote)
    {
      if (energy >= TilePlasmaHeater.power / 20)
      {
        if (tankInputDeuterium.getFluidAmount >= TilePlasmaHeater.plasmaHeatAmount && tankInputTritium.getFluidAmount >= TilePlasmaHeater.plasmaHeatAmount && tankOutput.getFluidAmount < tankOutput.getCapacity)
        {
          tankInputDeuterium.drain(TilePlasmaHeater.plasmaHeatAmount, true)
          tankInputTritium.drain(TilePlasmaHeater.plasmaHeatAmount, true)
          tankOutput.fill(new FluidStack(QuantumContent.FLUID_PLASMA, tankOutput.getCapacity), true)
          energy -= TilePlasmaHeater.power / 20
        }
      }
    }
    if (ticks % 80 == 0)
    {
      world.markBlockForUpdate(x.toInt, y.toInt, z.toInt)
    }
  }

  override def getDescriptionPacket: Packet =
  {
    val nbt: NBTTagCompound = new NBTTagCompound
    writeToNBT(nbt)
    return ResonantEngine.packetHandler.toMCPacket(new PacketTile(this, nbt))
  }

  /**
   * Writes a tile entity to NBT.
   */
  override def writeToNBT(nbt: NBTTagCompound)
  {
    super.writeToNBT(nbt)
    if (tankInputDeuterium.getFluid != null)
    {
      val compound: NBTTagCompound = new NBTTagCompound
      tankInputDeuterium.getFluid.writeToNBT(compound)
      nbt.setTag("tankInputDeuterium", compound)
    }
    if (tankInputTritium.getFluid != null)
    {
      val compound: NBTTagCompound = new NBTTagCompound
      tankInputTritium.getFluid.writeToNBT(compound)
      nbt.setTag("tankInputTritium", compound)
    }
    if (tankOutput.getFluid != null)
    {
      val compound: NBTTagCompound = new NBTTagCompound
      tankOutput.getFluid.writeToNBT(compound)
      nbt.setTag("tankOutput", compound)
    }
  }

  def read(data: ByteBuf, player: EntityPlayer, `type`: PacketType)
  {
    try
    {
      readFromNBT(ByteBufUtils.readTag(data))
    }
    catch
      {
        case e: Exception =>
        {
          e.printStackTrace
        }
      }
  }

  /**
   * Reads a tile entity from NBT.
   */
  override def readFromNBT(nbt: NBTTagCompound)
  {
    super.readFromNBT(nbt)
    val deuterium: NBTTagCompound = nbt.getCompoundTag("tankInputDeuterium")
    tankInputDeuterium.setFluid(FluidStack.loadFluidStackFromNBT(deuterium))
    val tritium: NBTTagCompound = nbt.getCompoundTag("tankInputTritium")
    tankInputTritium.setFluid(FluidStack.loadFluidStackFromNBT(tritium))
    val output: NBTTagCompound = nbt.getCompoundTag("tankOutput")
    tankOutput.setFluid(FluidStack.loadFluidStackFromNBT(output))
  }

  def addInformation(map: HashMap[String, Integer], player: EntityPlayer): Float =
  {
    if (energy != null)
    {
      map.put(LanguageUtility.getLocal("tooltip.energy") + ": " + new UnitDisplay(UnitDisplay.Unit.JOULES, energy.value), 0xFFFFFF)
    }
    if (tankInputDeuterium.getFluidAmount > 0)
    {
      map.put(LanguageUtility.getLocal("fluid.deuterium") + ": " + tankInputDeuterium.getFluidAmount + " L", 0xFFFFFF)
    }
    if (tankInputTritium.getFluidAmount > 0)
    {
      map.put(LanguageUtility.getLocal("fluid.tritium") + ": " + tankInputTritium.getFluidAmount + " L", 0xFFFFFF)
    }
    if (tankOutput.getFluidAmount > 0)
    {
      map.put(LanguageUtility.getLocal("fluid.plasma") + ": " + tankOutput.getFluidAmount + " L", 0xFFFFFF)
    }
    return 1.5f
  }

  def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean): Int =
  {
    if (resource.isFluidEqual(QuantumContent.FLUIDSTACK_DEUTERIUM))
    {
      return tankInputDeuterium.fill(resource, doFill)
    }
    if (resource.isFluidEqual(QuantumContent.getFluidStackTritium))
    {
      return tankInputTritium.fill(resource, doFill)
    }
    return 0
  }

  def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean): FluidStack =
  {
    return drain(from, resource.amount, doDrain)
  }

  def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean): FluidStack =
  {
    return tankOutput.drain(maxDrain, doDrain)
  }

  def canFill(from: ForgeDirection, fluid: Fluid): Boolean =
  {
    return fluid.getID == QuantumContent.FLUID_DEUTERIUM.getID || fluid.getID == QuantumContent.getFluidTritium.getID
  }

  def canDrain(from: ForgeDirection, fluid: Fluid): Boolean =
  {
    return fluid eq QuantumContent.FLUID_PLASMA
  }

  def getTankInfo(from: ForgeDirection): Array[FluidTankInfo] =
  {
    return Array[FluidTankInfo](tankInputDeuterium.getInfo, tankInputTritium.getInfo, tankOutput.getInfo)
  }

  override def use(player: EntityPlayer, side: Int, hit: Vector3): Boolean =
  {
    return FluidUtility.playerActivatedFluidItem(world, x.toInt, y.toInt, z.toInt, player, side)
  }

}
