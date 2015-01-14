package edx.mechanical.mech.grid

import edx.core.interfaces.TNodeMechanical
import edx.core.prefab.node.TMultipartNode
import resonant.api.tile.INodeProvider
import resonant.lib.debug.DebugInfo
import resonant.lib.grid.GridNode
import resonant.lib.grid.node.{NodeGrid, TTileConnector}
import resonant.lib.transform.vector.IVectorWorld

import scala.beans.BeanProperty
import scala.collection.convert.wrapAll._

/**
 * Prefab node for the mechanical system used by almost ever mechanical object in Resonant Induction. Handles connections to other tiles, and shares power with them
 *
 * @author Calclavia, Darkguardsman
 */
class NodeMechanical(parent: INodeProvider) extends NodeGrid[NodeMechanical](parent) with TTileConnector[NodeMechanical] with TMultipartNode[NodeMechanical] with TNodeMechanical with IVectorWorld with DebugInfo
{
  /**
   * Angle calculations
   */
  var prevTime = System.currentTimeMillis()
  var prevAngle = 0D
  /**
   * Events
   */
  @BeanProperty
  var onTorqueChanged: () => Unit = () => ()
  @BeanProperty
  var onVelocityChanged: () => Unit = () => ()
  protected[grid] var prevTorque = 0D
  protected[grid] var prevAngularVelocity = 0D
  /**
   * Buffer values used by the grid to transfer mechanical energy.
   */
  protected[grid] var bufferTorque = 0D
  protected[grid] var bufferAngularVelocity = 0D
  private var _torque = 0D
  private var _angularVelocity = 0D

  /**
   * An arbitrary angle value computed based on velocity
   * @return The angle in radians
   */
  def angle: Double =
  {
    val deltaTime = (System.currentTimeMillis() - prevTime) / 1000D
    prevTime = System.currentTimeMillis()
    prevAngle = (prevAngle + deltaTime * angularVelocity) % (2 * Math.PI)
    return prevAngle
  }

  /**
   * Sets the mechanical node's angle based on its connections
   */
  def resetAngle()
  {
    connections.foreach(
      n =>
      {
        val diff = Math.round((n.prevAngle - prevAngle) * angleDisplacement)
        n.prevAngle = (prevAngle + angleDisplacement) % (Math.PI * 2)
      }
    )
    prevTime = System.currentTimeMillis()
  }

  /**
   * The amount of angle in radians displaced. This is used to align the gear teeth.
   */
  def angleDisplacement = 0D

  override def rotate(torque: Double, angularVelocity: Double)
  {
    bufferTorque += torque
    bufferAngularVelocity += angularVelocity
  }

  def power: Double = torque * angularVelocity

  /**
   * Gets the angular velocity of the mechanical device from a specific side
   *
   * @return Angular velocity in meters per second
   */
  override def angularVelocity = _angularVelocity

  def angularVelocity_=(newVel: Double) = _angularVelocity = newVel

  /**
   * Gets the torque of the mechanical device from a specific side
   *
   * @return force
   */
  override def torque = _torque

  def torque_=(newTorque: Double) = _torque = newTorque

  def getMechanicalGrid: MechanicalGrid = super.grid.asInstanceOf[MechanicalGrid]

  override def newGrid: GridNode[NodeMechanical] = new MechanicalGrid

  override def isValidConnection(other: AnyRef): Boolean = other.isInstanceOf[NodeMechanical]

  override def getDebugInfo = List(toString)

  override def toString = "NodeMechanical [" + connections.size() + " Torque: " + BigDecimal(torque).setScale(2, BigDecimal.RoundingMode.HALF_UP) + " Velocity: " + BigDecimal(angularVelocity).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "]"

  /**
   * The class used to compare when making connections
   */
  override protected def getCompareClass = classOf[NodeMechanical]
}