package edx.electrical.battery

import java.util.ArrayList

import cpw.mods.fml.relauncher.{Side, SideOnly}
import edx.core.Reference
import io.netty.buffer.ByteBuf
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.AdvancedModelLoader
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.opengl.GL11._
import resonant.lib.content.prefab.{TElectric, TEnergyStorage, TIO}
import resonant.lib.grid.energy.EnergyStorage
import resonant.lib.network.discriminator.PacketType
import resonant.lib.network.handle.{TPacketReceiver, TPacketSender}
import resonant.lib.prefab.tile.spatial.SpatialTile
import resonant.lib.render.RenderUtility
import resonant.lib.transform.vector.Vector3
import resonant.lib.wrapper.ByteBufWrapper._

/** A modular battery box that allows shared connections with boxes next to it.
  *
  * @author Calclavia
  */
object TileBattery
{
  /** Tiers: 0, 1, 2 */
  final val maxTier = 2

  @SideOnly(Side.CLIENT)
  val model = AdvancedModelLoader.loadModel(new ResourceLocation(Reference.domain, Reference.modelPath + "battery/battery.tcn"))

  /**
   * @param tier - 0, 1, 2
   * @return
   */
  def getEnergyForTier(tier: Int) = Math.round(Math.pow(500000000, (tier / (maxTier + 0.7f)) + 1) / 500000000) * 500000000
}

class TileBattery extends SpatialTile(Material.iron) with TIO with TElectric with TPacketSender with TPacketReceiver with TEnergyStorage
{
  var renderEnergyAmount = 0d
  var doCharge = false
  private var markClientUpdate = false
  private var markDistributionUpdate = false

  energy = new EnergyStorage
  textureName = "material_metal_side"
  ioMap = 0
  saveIOMap = true
  normalRender = false
  isOpaqueCube = false
  itemBlock = classOf[ItemBlockBattery]
  dcNode.resistance = 10

  override def start()
  {
    super.start()
    updateConnectionMask()
  }

  def updateConnectionMask()
  {
    dcNode.connectionMask = ForgeDirection.VALID_DIRECTIONS.filter(getIO(_) > 0).map(d => 1 << d.ordinal()).foldLeft(0)(_ | _)
    dcNode.positiveTerminals.addAll(getInputDirections())
    dcNode.negativeTerminals.addAll(getOutputDirections())
    notifyChange()
  }

  override def update()
  {
    super.update()

    if (!world.isRemote)
    {
      //TODO: Voltage of battery should decrease over time.
      //TODO: Test, remove this
      if (doCharge)
        dcNode.setVoltage(500)
    }
  }

  override def write(buf: ByteBuf, id: Int)
  {
    super.write(buf, id)
    buf <<< renderEnergyAmount
    buf <<< ioMap
  }

  override def read(buf: ByteBuf, id: Int, packetType: PacketType)
  {
    super.read(buf, id, packetType)
    energy.setEnergy(buf.readDouble())
    ioMap = buf.readInt()
  }

  override def setIO(dir: ForgeDirection, packet: Int)
  {
    super.setIO(dir, packet)
    updateConnectionMask()
    dcNode.reconstruct()
    markUpdate()
  }

  override def onPlaced(entityLiving: EntityLivingBase, itemStack: ItemStack)
  {
    if (!world.isRemote && itemStack.getItem.isInstanceOf[ItemBlockBattery])
    {
      energy.setCapacity(TileBattery.getEnergyForTier(ItemBlockBattery.getTier(itemStack)))
      energy.setEnergy(itemStack.getItem.asInstanceOf[ItemBlockBattery].getEnergy(itemStack))
      world.setBlockMetadataWithNotify(xi, yi, zi, ItemBlockBattery.getTier(itemStack), 3)
    }
  }

  override def getDrops(metadata: Int, fortune: Int): ArrayList[ItemStack] =
  {
    val ret = new ArrayList[ItemStack]
    val itemStack: ItemStack = new ItemStack(getBlockType, 1)
    val itemBlock: ItemBlockBattery = itemStack.getItem.asInstanceOf[ItemBlockBattery]
    ItemBlockBattery.setTier(itemStack, world.getBlockMetadata(xi, yi, zi).asInstanceOf[Byte])
    itemBlock.setEnergy(itemStack, energy.getEnergy)
    ret.add(itemStack)
    return ret
  }

  @SideOnly(Side.CLIENT)
  override def renderInventory(itemStack: ItemStack)
  {
    glPushMatrix()
    val energyLevel = ((itemStack.getItem.asInstanceOf[ItemBlockBattery].getEnergy(itemStack) / itemStack.getItem.asInstanceOf[ItemBlockBattery].getEnergyCapacity(itemStack)) * 8).toInt
    RenderUtility.bind(Reference.domain, Reference.modelPath + "battery/battery.png")
    var disabledParts = Set.empty[String]
    disabledParts ++= Set("connector", "connectorIn", "connectorOut")
    disabledParts ++= Set("coil1", "coil2", "coil3", "coil4", "coil5", "coil6", "coil7", "coil8")
    disabledParts ++= Set("coil1lit", "coil2lit", "coil3lit", "coil4lit", "coil5lit", "coil6lit", "coil7lit", "coil8lit")
    disabledParts ++= Set("frame1con", "frame2con", "frame3con", "frame4con")
    TileBattery.model.renderAllExcept(disabledParts.toList: _*)

    for (i <- 1 until 8)
    {
      if (i != 1 || !disabledParts.contains("coil1"))
      {
        if ((8 - i) <= energyLevel)
          TileBattery.model.renderOnly("coil" + i + "lit")
        else
          TileBattery.model.renderOnly("coil" + i)
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
    val energyLevel = Math.round((energy.getEnergy / TileBattery.getEnergyForTier(getBlockMetadata).asInstanceOf[Double]) * 8).toInt

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
          case 1 => TileBattery.model.renderOnly("connectorIn")
          case 2 => TileBattery.model.renderOnly("connectorOut")
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
        if ((8 - i) < energyLevel)
          TileBattery.model.renderOnly("coil" + i + "lit")
        else
          TileBattery.model.renderOnly("coil" + i)
      }
    }

    disabledParts ++= Set("connector", "connectorIn", "connectorOut")
    disabledParts ++= Set("coil1", "coil2", "coil3", "coil4", "coil5", "coil6", "coil7", "coil8")
    disabledParts ++= Set("coil1lit", "coil2lit", "coil3lit", "coil4lit", "coil5lit", "coil6lit", "coil7lit", "coil8lit")
    disabledParts ++= Set("frame1con", "frame2con", "frame3con", "frame4con")
    enabledParts --= Set("coil1", "coil2", "coil3", "coil4", "coil5", "coil6", "coil7", "coil8")
    TileBattery.model.renderAllExcept(disabledParts.toList: _*)
    TileBattery.model.renderOnly(enabledParts.toList: _*)
    glPopMatrix()
  }

  override def toString: String = "[TileBattery]" + x + "x " + y + "y " + z + "z "

  override protected def use(player: EntityPlayer, side: Int, hit: Vector3): Boolean =
  {
    super.use(player, side, hit)

    if (!world.isRemote)
    {
      if (player.isSneaking)
      {
        doCharge = !doCharge
        println("Charge: " + doCharge)
      }
    }

    return true
  }
}