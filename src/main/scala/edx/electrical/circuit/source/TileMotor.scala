package edx.electrical.circuit.source

import cpw.mods.fml.relauncher.{Side, SideOnly}
import edx.core.Reference
import edx.electrical.Models
import edx.mechanical.mech.grid.NodeMechanical
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{ChatComponentText, ResourceLocation}
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.opengl.GL11
import resonantengine.lib.content.prefab.TIO
import resonantengine.lib.grid.energy.electric.NodeElectricComponent
import resonantengine.lib.modcontent.block.ResonantTile
import resonantengine.lib.render.RenderUtility
import resonantengine.lib.transform.vector.Vector3
import resonantengine.prefab.block.impl.{TBlockNodeProvider, TRotatable}

/**
 * A kinetic energy to electrical energy converter.
 *
 * @author Calclavia
 */
object TileMotor
{
  @SideOnly(Side.CLIENT)
  val texture = new ResourceLocation(Reference.domain, Reference.modelPath + "motor.png")

  val fieldStrength = 1
  val coils = 10
  val area = 1

  /**
   * Motor constant is the product of: N (Number of coils), B (Magnetic Field Density), A (Area)
   * Or, we can call it: N * Total Flux
   */
  val motorConstant = fieldStrength * area * coils
}

class TileMotor extends ResonantTile(Material.iron) with TIO with TBlockNodeProvider with TRotatable
{
  private val electricNode = new NodeElectricComponent(this)
  private val mechNode = new NodeMechanical(this)
  {
    override def canConnect(from: ForgeDirection): Boolean =
    {
      connectionMask = 1 << getDirection.getOpposite.ordinal
      return super.canConnect(from)
    }
  }

  private var gearRatio = 0

  textureName = "material_wood_surface"
  normalRender = false
  isOpaqueCube = false
  ioMap = 0
  saveIOMap = true
  nodes.add(electricNode)
  nodes.add(mechNode)

  electricNode.resistance = 10

  def toggleGearRatio() = (gearRatio + 1) % 3

  override def start()
  {
    updateConnections()
    super.start()
  }

  override def update()
  {
    super.update()

    /**
     * Produce torque based on current.
     * T = NBA * I / (2pi)
     */
    val torque = TileMotor.motorConstant * electricNode.current / (2 * Math.PI)
    mechNode.accelerate(torque)

    /**
     * Motors produce emf or counter-emf by Lenz's law based on angular velocity
     * emf = change of flux/time
     *
     * After differentiation via chain rule:
     * emf = NBAwSin(wt)
     * emfMax = NBAw for direct current
     * where w = angular velocity
     */
    val inducedEmf = TileMotor.motorConstant * mechNode.angularVelocity
    electricNode.generateVoltage(inducedEmf)
  }

  override def setIO(dir: ForgeDirection, ioType: Int)
  {
    if (dir != getDirection || dir != getDirection.getOpposite)
    {
      super.setIO(dir, ioType)

      //Auto-set opposite side for unreachable sides
      if (ioType != 0)
        super.setIO(dir.getOpposite, (ioType % 2) + 1)
      updateConnections()
    }
  }

  def updateConnections()
  {
    electricNode.setPositives(getInputDirections())
    electricNode.setNegatives(getOutputDirections())
    electricNode.reconstruct()
    notifyChange()
    markUpdate()
  }

  @SideOnly(Side.CLIENT)
  override def renderDynamic(pos: Vector3, frame: Float, pass: Int): Unit =
  {
    GL11.glPushMatrix()
    GL11.glTranslatef(pos.x.toFloat + 0.5f, pos.y.toFloat + 0.5f, pos.z.toFloat + 0.5f)
    GL11.glRotatef(90, 0, 1, 0)
    RenderUtility.rotateBlockBasedOnDirection(getDirection)
    RenderUtility.bind(TileMotor.texture)
    Models.motor.renderAll()
    GL11.glPopMatrix()
  }

  override def readFromNBT(nbt: NBTTagCompound)
  {
    super[TIO].readFromNBT(nbt)
    super.readFromNBT(nbt)
    gearRatio = nbt.getByte("gear")
  }

  override def writeToNBT(nbt: NBTTagCompound)
  {
    super[TIO].writeToNBT(nbt)
    super.writeToNBT(nbt)
    nbt.setByte("gear", gearRatio.toByte)
  }

  override def toString: String = "[TileMotor]" + x + "x " + y + "y " + z + "z "

  override protected def configure(player: EntityPlayer, side: Int, hit: Vector3): Boolean =
  {
    if (player.isSneaking)
    {
      if (!world.isRemote)
      {
        gearRatio = (gearRatio + 1) % 3
        player.addChatComponentMessage(new ChatComponentText("Toggled gear ratio: " + gearRatio))
      }
      return true
    }

    return super.configure(player, side, hit)
  }

}
