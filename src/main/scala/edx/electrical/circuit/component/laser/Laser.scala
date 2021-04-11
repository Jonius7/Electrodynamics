package edx.electrical.circuit.component.laser

import java.awt.Color

import cpw.mods.fml.relauncher.ReflectionHelper
import edx.core.Electrodynamics
import net.minecraft.block.material.Material
import net.minecraft.block.{Block, BlockStainedGlass, BlockStainedGlassPane, BlockTNT}
import net.minecraft.init.Blocks
import net.minecraft.item.ItemDye
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.{DamageSource, MovingObjectPosition}
import net.minecraft.world.World
import resonantengine.lib.transform.vector.Vector3

import scala.collection.mutable

/**
 * Handles laser interaction
 * @author Calclavia
 */
object Laser
{
  val maxDistance = 100

  val minEnergy = 100d
  val maxEnergy = 20000d

  val minEnergyToMine = 10000d
  val maxEnergyToMine = 500000d
  val minBurnEnergy = minEnergyToMine
  val currentBlockEnergy = mutable.HashMap[Vector3, Double]()
  val accumilatedBlockEnergy = mutable.HashMap[Vector3, Double]()
  var lastUpdateTime = 0L

  def spawn(world: World, start: Vector3, direction: Vector3, energy: Double)
  {
    spawn(world, start, start, direction, energy)
  }

  def spawn(world: World, start: Vector3, renderStart: Vector3, direction: Vector3, energy: Double)
  {
    spawn(world, start, renderStart, direction, new Vector3(1, 1, 1), energy)
  }

  def spawn(world: World, start: Vector3, renderStart: Vector3, direction: Vector3, color: Vector3, energy: Double)
  {
    if (energy > minEnergy)
    {
      val maxPos = start + (direction * maxDistance)
      val hit = start.rayTrace(world, maxPos)

      if (hit != null)
      {
        if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
          val hitVec = new Vector3(hit.hitVec)
          val hitBlockPos = new Vector3(hit.blockX, hit.blockY, hit.blockZ)
          /**
           * Handle Mirror Reflection
           */
          val hitTile = world.getTileEntity(hit.blockX, hit.blockY, hit.blockZ)
          val hitBlock = world.getBlock(hit.blockX, hit.blockY, hit.blockZ)
          val hitMetadata = world.getBlockMetadata(hit.blockX, hit.blockY, hit.blockZ)

          if (hitTile.isInstanceOf[ILaserHandler])
          {
            if (!hitTile.asInstanceOf[ILaserHandler].onLaserHit(renderStart, direction, hit, color, energy))
            {
              Electrodynamics.proxy.renderLaser(world, renderStart, hitVec, color, energy)
            }
          }
          else if (hitBlock.getMaterial == Material.glass)
          {
            Electrodynamics.proxy.renderLaser(world, renderStart, hitVec, color, energy)
            var newColor = color

            if (hitBlock.isInstanceOf[BlockStainedGlass] || hitBlock.isInstanceOf[BlockStainedGlassPane])
            {
              val dyeColor = new Color(ItemDye.field_150922_c(blockToDye(hitMetadata)))
              newColor = new Vector3(dyeColor.getRed, dyeColor.getGreen, dyeColor.getBlue).normalize
            }

            spawn(world, hitVec + direction * 0.9, hitVec, direction, ((newColor + color) / 2).normalize, energy / 1.05)
          }
          else
          {
            /**
             * Attempt to burn block
             */
            if (!world.isRemote && Electrodynamics.ftbcompat.canDestroyBlock(hitBlockPos.toVec3, start.toVec3, world))
            {
              val hardness = hitBlock.getBlockHardness(world, hitBlockPos.x.toInt, hitBlockPos.y.toInt, hitBlockPos.z.toInt)

              if (hardness != -1)
              {
                if (lastUpdateTime != world.getWorldTime)
                {
                  currentBlockEnergy.clear
                  lastUpdateTime = world.getWorldTime
                }

                val energyOnBlock = (if (currentBlockEnergy.contains(hitBlockPos)) currentBlockEnergy(hitBlockPos) else 0) + energy
                currentBlockEnergy.put(hitBlockPos, energyOnBlock)

                if (hitTile.isInstanceOf[TileEntityFurnace])
                {
                  /**
                   * Cook in furnace
                   */
                  val furnace = hitTile.asInstanceOf[TileEntityFurnace]

                  try
                  {
                    if (ReflectionHelper.findMethod(classOf[TileEntityFurnace], furnace, Array("canSmelt", "func_145948_k")).invoke(furnace).asInstanceOf[Boolean])
                    {
                      furnace.furnaceBurnTime = Math.max(2, furnace.furnaceBurnTime)
                      furnace.furnaceCookTime = Math.min(199, furnace.furnaceCookTime + (15 * (energy / maxEnergy)).toInt)
                    }
                  }
                  catch
                    {
                      case e: Exception => e.printStackTrace()
                    }
                }
                else
                {
                  if (energyOnBlock > minEnergyToMine)
                  {
                    /**
                     * The laser can mine the hitBlock!
                     */
                    val accumulatedEnergy = (if (accumilatedBlockEnergy.contains(hitBlockPos)) accumilatedBlockEnergy(hitBlockPos) else 0) + energy
                    accumilatedBlockEnergy.put(hitBlockPos, accumulatedEnergy)

                    val energyRequiredToMineBlock = hardness * maxEnergyToMine

                    world.destroyBlockInWorldPartially(Block.blockRegistry.getIDForObject(hitBlock), hitBlockPos.x.toInt, hitBlockPos.y.toInt, hitBlockPos.z.toInt, (accumulatedEnergy / energyRequiredToMineBlock * 10).toInt)

                    if (accumulatedEnergy > energyRequiredToMineBlock)
                    {
                      hitBlock.dropBlockAsItem(world, hitBlockPos.x.toInt, hitBlockPos.y.toInt, hitBlockPos.z.toInt, world.getBlockMetadata(hitBlockPos.x.toInt, hitBlockPos.y.toInt, hitBlockPos.z.toInt), 0)
                      world.setBlockToAir(hitBlockPos.x.toInt, hitBlockPos.y.toInt, hitBlockPos.z.toInt)

                      accumilatedBlockEnergy.remove(hitBlockPos)
                    }
                  }
                  else
                  {
                    //accumilatedBlockEnergy.remove(hitBlockPos)
                  }

                  /**
                   * Catch Fire
                   */
                  if (energyOnBlock > minBurnEnergy && hitBlock.getMaterial.getCanBurn)
                  {
                    if (hitBlock.isInstanceOf[BlockTNT])
                    {
                      hitBlock.asInstanceOf[BlockTNT].func_150114_a(world, hitBlockPos.x.toInt, hitBlockPos.y.toInt, hitBlockPos.z.toInt, 1, null)
                    }
                    world.setBlock(hitBlockPos.x.toInt, hitBlockPos.y.toInt, hitBlockPos.z.toInt, Blocks.fire)
                  }
                }
              }
            }

            /**
             * Render laser hit
             */
            Electrodynamics.proxy.renderLaser(world, renderStart, hitVec, color, energy)

            /**
             * Render scorch and particles
             */
            Electrodynamics.proxy.renderScorch(world, hitVec - (direction * 0.02), hit.sideHit)
            Electrodynamics.proxy.renderBlockParticle(world, hitVec, hitBlock, hit.sideHit)
          }
        }
        else if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
        {
          if (energy > minBurnEnergy)
          {
            val fireTime = (10 * (energy / maxEnergy)).toInt

            if (fireTime > 0)
            {
              hit.entityHit.setFire(fireTime)
              hit.entityHit.attackEntityFrom(DamageSource.onFire, (20 * (energy / maxEnergy)).toInt)
            }
          }

          Electrodynamics.proxy.renderLaser(world, renderStart, new Vector3(hit.hitVec), color, energy)
        }

        return
      }

      Electrodynamics.proxy.renderLaser(world, renderStart, maxPos, color, energy)
    }
  }

  def blockToDye(blockMeta: Int): Int =
  {
    return ~blockMeta & 15
  }
}
