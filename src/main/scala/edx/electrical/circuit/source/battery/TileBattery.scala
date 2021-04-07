package edx.electrical.circuit.source.battery

import java.util.ArrayList

import cofh.api.energy.{IEnergyHandler, IEnergyReceiver}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import edx.core.Reference
import edx.electrical.Models
import io.netty.buffer.ByteBuf
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.opengl.GL11._
import resonantengine.api.item.ISimpleItemRenderer
import resonantengine.core.network.discriminator.PacketType
import resonantengine.lib.content.prefab.TIO
import resonantengine.lib.grid.energy.EnergyStorage
import resonantengine.lib.grid.energy.electric.NodeElectricComponent
import resonantengine.lib.modcontent.block.ResonantTile
import resonantengine.lib.render.RenderUtility
import resonantengine.lib.transform.vector.Vector3
import resonantengine.lib.utility.science.UnitDisplay
import resonantengine.lib.wrapper.ByteBufWrapper._
import resonantengine.prefab.block.impl.{TBlockNodeProvider, TEnergyProvider}
import resonantengine.prefab.network.{TPacketReceiver, TPacketSender}

/** A modular battery box that allows shared connections with boxes next to it.
  *
  * @author Calclavia
  */
object TileBattery
{
  /** Tiers: 0, 1, 2 */
  final val maxTier = 2

  /**
   * @param tier - 0, 1, 2
   * @return
   */
  def getEnergyForTier(tier: Int) = Math.round(Math.pow(500000000, (tier / (maxTier + 0.7f)) + 1) / 500000000) * 500000000
}

class TileBattery extends ResonantTile(Material.iron) with TIO with TBlockNodeProvider with TPacketSender with TPacketReceiver with TEnergyProvider with ISimpleItemRenderer with IEnergyHandler
{
  private val electricNode = new NodeElectricComponent(this)
  var energyRenderLevel = 0

  nodes.add(electricNode)
  energy = new EnergyStorage
  textureName = "material_metal_side"
  ioMap = 0
  saveIOMap = true
  normalRender = false
  isOpaqueCube = false
  itemBlock = classOf[ItemBlockBattery]
  electricNode.resistance = 10

  override def start()
  {
    super.start()
    updateConnectionMask()
  }

  override def update()
  {
    super.update()

    if (!world.isRemote)
    {
      if (isIndirectlyPowered)
      {
        //Discharge battery when current is flowing positive direction
        //TODO: Allow player to set the power output
        electricNode.generatePower(Math.min(energy.max * 0.0001, energy.value))
        val dissipatedEnergy = electricNode.power / 20
        energy -= dissipatedEnergy
      }
      else
      {
        //Recharge battery when current is flowing negative direction
        energy += electricNode.power / 20
      }

      for (side <- ForgeDirection.VALID_DIRECTIONS) {
        if (getOutputDirections().contains(side)) {
          val tile = worldObj.getTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ)
          if (tile != null && tile.isInstanceOf[IEnergyReceiver]) {
            val rec = tile.asInstanceOf[IEnergyReceiver]
            val received = Math.min(rec.receiveEnergy(side.getOpposite, (energy.value / 50).asInstanceOf[Int], true), (energy.value / 50).asInstanceOf[Int])
            energy -= (received * 50)
            rec.receiveEnergy(side.getOpposite, received, false)
          }
        }
      }


      if (energy.prev != energy.value)
      {
        energyRenderLevel = Math.round((energy.value / TileBattery.getEnergyForTier(getBlockMetadata).toDouble) * 8).toInt
        sendDescPacket()
      }

      /**
       * Update packet when energy level changes.

      val prevEnergyLevel = energyRenderLevel
      energyRenderLevel = Math.round((energy.value / TileBattery.getEnergyForTier(getBlockMetadata).toDouble) * 8).toInt

      if (prevEnergyLevel != energyRenderLevel)
      {
        markUpdate()
      }*/
    }
  }

  override def write(buf: ByteBuf, id: Int)
  {
    super.write(buf, id)
    buf <<< energy.value
    buf <<< energyRenderLevel.toByte
    buf <<< ioMap
  }

  override def read(buf: ByteBuf, id: Int, packetType: PacketType)
  {
    super.read(buf, id, packetType)
    energy.max = TileBattery.getEnergyForTier(metadata)
    energy.value = buf.readDouble()
    energyRenderLevel = buf.readByte()
    ioMap = buf.readInt()
  }

  override def setIO(dir: ForgeDirection, packet: Int)
  {
    super.setIO(dir, packet)
    updateConnectionMask()
  }

  def updateConnectionMask()
  {
    electricNode.setPositives(getInputDirections())
    electricNode.setNegatives(getOutputDirections())
    electricNode.reconstruct()
    markUpdate()
    notifyChange()
  }

  override def onPlaced(entityLiving: EntityLivingBase, itemStack: ItemStack)
  {
    if (!world.isRemote && itemStack.getItem.isInstanceOf[ItemBlockBattery])
    {
      energy.max = (TileBattery.getEnergyForTier(ItemBlockBattery.getTier(itemStack)))
      energy.value = itemStack.getItem.asInstanceOf[ItemBlockBattery].getEnergy(itemStack)
      world.setBlockMetadataWithNotify(x.toInt, y.toInt, z.toInt, ItemBlockBattery.getTier(itemStack), 3)
    }
  }

  override def getDrops(metadata: Int, fortune: Int): ArrayList[ItemStack] =
  {
    val ret = new ArrayList[ItemStack]
    val itemStack: ItemStack = new ItemStack(getBlockType, 1)
    val itemBlock: ItemBlockBattery = itemStack.getItem.asInstanceOf[ItemBlockBattery]
    ItemBlockBattery.setTier(itemStack, world.getBlockMetadata(x.toInt, y.toInt, z.toInt).asInstanceOf[Byte])
    itemBlock.setEnergy(itemStack, energy.value)
    ret.add(itemStack)
    return ret
  }

  @SideOnly(Side.CLIENT)
  override def renderInventoryItem(`type`: ItemRenderType, itemStack: ItemStack, data: AnyRef*)
  {
    glPushMatrix()
    val energyLevel = ((itemStack.getItem.asInstanceOf[ItemBlockBattery].getEnergy(itemStack) / itemStack.getItem.asInstanceOf[ItemBlockBattery].getEnergyCapacity(itemStack)) * 8).toInt
    RenderUtility.bind(Reference.domain, Reference.modelPath + "battery/battery.png")
    var disabledParts = Set.empty[String]
    disabledParts ++= Set("connector", "connectorIn", "connectorOut")
    disabledParts ++= Set("coil1", "coil2", "coil3", "coil4", "coil5", "coil6", "coil7", "coil8")
    disabledParts ++= Set("coil1lit", "coil2lit", "coil3lit", "coil4lit", "coil5lit", "coil6lit", "coil7lit", "coil8lit")
    disabledParts ++= Set("frame1con", "frame2con", "frame3con", "frame4con")
    Models.battery.renderAllExcept(disabledParts.toList: _*)

    for (i <- 1 until 8)
    {
      if (i != 1 || !disabledParts.contains("coil1"))
      {
        if ((8 - i) <= energyLevel)
          Models.battery.renderOnly("coil" + i + "lit")
        else
          Models.battery.renderOnly("coil" + i)
      }
    }
    glPopMatrix()
  }

  @SideOnly(Side.CLIENT)
  override def renderDynamic(pos: Vector3, frame: Float, pass: Int)
  {
    val partToDisable = Array[Array[String]](Array[String]("bottom"), Array[String]("top"), Array[String]("frame1", "frame2"), Array[String]("frame3", "frame4"), Array[String]("frame4", "frame1"), Array[String]("frame2", "frame3"))
    val connectionPartToEnable = Array[Array[String]](null, null, Array[String]("frame1con", "frame2con"), Array[String]("frame3con", "frame4con"), Array[String]("frame4con", "frame1con"), Array[String]("frame2con", "frame3con"))
    glPushMatrix()
    glTranslated(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
    RenderUtility.bind(Reference.domain, Reference.modelPath + "battery/battery.png")

    var disabledParts = Set.empty[String]
    var enabledParts = Set.empty[String]

    for (check <- ForgeDirection.VALID_DIRECTIONS)
    {
      if ((toVectorWorld + check).getTileEntity.isInstanceOf[TileBattery])
      {
        disabledParts ++= partToDisable(check.ordinal)
        if (check == ForgeDirection.UP)
        {
          enabledParts ++= partToDisable(check.ordinal)
          enabledParts += "coil1"
        }
        else if (check == ForgeDirection.DOWN)
        {
          var connectionParts = Set.empty[String]
          val downDirs = ForgeDirection.VALID_DIRECTIONS.filter(_.offsetY == 0)
          downDirs.foreach(s => connectionParts ++= connectionPartToEnable(s.ordinal))
          downDirs.filter(s => (toVectorWorld + s).getTileEntity.isInstanceOf[TileBattery]).foreach(s => connectionParts --= connectionPartToEnable(s.ordinal))
          enabledParts ++= connectionParts
        }
      }

      //Render connectors
      if (check.offsetY == 0)
      {
        glPushMatrix()
        RenderUtility.rotateBlockBasedOnDirection(check)

        glRotatef(-90, 0, 1, 0)

        getIO(check) match
        {
          case 1 => Models.battery.renderOnly("connectorIn")
          case 2 => Models.battery.renderOnly("connectorOut")
          case _ =>
        }

        glPopMatrix()
      }
    }

    enabledParts --= disabledParts

    for (i <- 1 to 8)
    {
      if (i != 1 || enabledParts.contains("coil1"))
      {
        if ((8 - i) < energyRenderLevel)
          Models.battery.renderOnly("coil" + i + "lit")
        else
          Models.battery.renderOnly("coil" + i)
      }
    }

    disabledParts ++= Set("connector", "connectorIn", "connectorOut")
    disabledParts ++= Set("coil1", "coil2", "coil3", "coil4", "coil5", "coil6", "coil7", "coil8")
    disabledParts ++= Set("coil1lit", "coil2lit", "coil3lit", "coil4lit", "coil5lit", "coil6lit", "coil7lit", "coil8lit")
    disabledParts ++= Set("frame1con", "frame2con", "frame3con", "frame4con")
    enabledParts --= Set("coil1", "coil2", "coil3", "coil4", "coil5", "coil6", "coil7", "coil8")
    Models.battery.renderAllExcept(disabledParts.toList: _*)
    Models.battery.renderOnly(enabledParts.toList: _*)

    /**
     * Render energy tooltip
     */
    if (isPlayerLooking(Minecraft.getMinecraft.thePlayer))
      RenderUtility.renderFloatingText(new UnitDisplay(UnitDisplay.Unit.JOULES, energy.value).symbol + " / " + new UnitDisplay(UnitDisplay.Unit.JOULES, energy.max).symbol, new Vector3(0, 0.9, 0))
    glPopMatrix()
  }

  override def readFromNBT(nbt: NBTTagCompound)
  {
    super.readFromNBT(nbt)
    energy.load(nbt)
  }

  override def writeToNBT(nbt: NBTTagCompound)
  {
    super.writeToNBT(nbt)
    energy.save(nbt)
  }

  override def receiveEnergy(side: ForgeDirection, i: Int, b: Boolean): Int = {
    val joule: Double = i * 50
    val maxReceive = Math.min(joule, energy.max - energy.value)
    if (!b) {
      energy.setValue(energy.value + maxReceive)
      markUpdate()
    }
    return (maxReceive / 50).asInstanceOf[Int]
  }

  override def extractEnergy(side: ForgeDirection, i: Int, b: Boolean): Int = {
    val joule: Double = i * 50
    val maxExtract = Math.min(joule, energy.value)
    if (!b) {
      energy.setValue(energy.value - maxExtract)
      markUpdate()
    }
    return (maxExtract / 50).asInstanceOf[Int]
  }

  override def getEnergyStored(side: ForgeDirection): Int = {
    (energy.value / 50).asInstanceOf[Int]
  }

  override def getMaxEnergyStored(side: ForgeDirection): Int = {
    (energy.max / 50).asInstanceOf[Int]
  }

  override def canConnectEnergy(side: ForgeDirection): Boolean = {
    getOutputDirections().contains(side) || getInputDirections().contains(side)
  }
}
