package edx.quantum.machine.quantum

import edx.core.Reference
import edx.quantum.QuantumContent
import io.netty.buffer.ByteBuf
import net.minecraft.block.material.Material
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import resonantengine.api.edx.recipe.QuantumAssemblerRecipes
import resonantengine.api.network.IPacketReceiver
import resonantengine.core.network.discriminator.{PacketTile, PacketType}
import resonantengine.lib.content.prefab.TInventory
import resonantengine.lib.grid.energy.EnergyStorage
import resonantengine.lib.grid.energy.electric.NodeElectricComponent
import resonantengine.lib.modcontent.block.ResonantTile
import resonantengine.lib.transform.vector.Vector3
import resonantengine.prefab.block.impl.{TBlockNodeProvider, TEnergyProvider}

/**
 * Atomic assembler of items *
 *
 * @author Calclavia, Darkguardsman
 */
class TileQuantumAssembler extends ResonantTile(Material.iron) with TInventory with TBlockNodeProvider with IPacketReceiver with TEnergyProvider
{
  private val electricNode = new NodeElectricComponent(this)

  private[quantum] var power: Long = 1000000000L
  private[quantum] var MAX_TIME: Int = 20 * 120
  private[quantum] var time: Int = 0
  /**
   * Used for rendering arm motion, X Y Z are not used as location data
   */
  private[quantum] var rotation: Vector3 = new Vector3
  /**
   * Used for rendering.
   */
  private[quantum] var entityItem: EntityItem = null

  //Constructor

  //TODO: Dummy
  energy = new EnergyStorage
  energy.max = power
  isOpaqueCube = false
  normalRender = false
  customItemRender = true
  textureName = "machine"
  nodes.add(electricNode)

  override def getSizeInventory: Int = 7

  /**
   * Called when the block is right clicked by the player
   */
  override def use(player: EntityPlayer, side: Int, hit: Vector3): Boolean =
  {
    if (!world.isRemote)
    {
      player.openGui(QuantumContent, 0, world, x.toInt, y.toInt, z.toInt)
    }
    return true
  }

  override def update
  {
    super.update
    if (!this.worldObj.isRemote)
    {
      if (this.canProcess)
      {
        if (energy >= power / 20)
        {
          if (this.time == 0)
          {
            this.time = this.MAX_TIME
          }
          if (this.time > 0)
          {
            this.time -= 1
            if (this.time < 1)
            {
              this.process
              this.time = 0
            }
          }
          else
          {
            this.time = 0
          }
          energy -= power / 20
        }
      }
      else
      {
        this.time = 0
      }
      if (this.ticks % 10 == 0)
      {
        //TODO send packets to each player with the GUI open
      }
    }
    else if (this.time > 0)
    {
      var middleStack: ItemStack = this.getStackInSlot(6)
      if (middleStack != null)
      {
        middleStack = middleStack.copy
        middleStack.stackSize = 1
        if (this.entityItem == null)
        {
          this.entityItem = new EntityItem(this.worldObj, 0, 0, 0, middleStack)
        }
        else if (!middleStack.isItemEqual(this.entityItem.getEntityItem))
        {
          this.entityItem = new EntityItem(this.worldObj, 0, 0, 0, middleStack)
        }
        this.entityItem.age += 1
      }
      else
      {
        this.entityItem = null
      }
      if (this.ticks % 600 == 0)
      {
        this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, Reference.prefix + "assembler", 0.7f, 1f)
      }
      this.rotation.add(3, 2, 1)
    }
  }

  /**
   * Turn one item from the furnace source stack into the appropriate smelted item in the furnace
   * result stack
   */
  def process
  {
    if (this.canProcess)
    {
      for (i <- 0 to 6)
      {
        if (getStackInSlot(i) != null)
        {
          decrStackSize(i, 1)
        }
      }
      if (getStackInSlot(6) != null)
      {
        getStackInSlot(6).stackSize += 1
      }
    }
  }

  /**
   * Checks to see if the assembler can run
   */
  def canProcess: Boolean =
  {
    if (getStackInSlot(6) != null)
    {
      if (QuantumAssemblerRecipes.hasItemStack(getStackInSlot(6)))
      {
        for (i <- 0 to 6)
        {
          if (getStackInSlot(i) == null)
          {
            return false
          }
          if (getStackInSlot(i).getItem ne QuantumContent.itemDarkMatter)
          {
            return false
          }
        }
        return getStackInSlot(6).stackSize < 64
      }
    }
    return false
  }

  def read(data: ByteBuf, player: EntityPlayer, `type`: PacketType)
  {
    this.time = data.readInt
    val itemID: Int = data.readInt
    val itemAmount: Int = data.readInt
    val itemMeta: Int = data.readInt
    if (itemID != -1 && itemAmount != -1 && itemMeta != -1)
    {
      this.setInventorySlotContents(6, new ItemStack(Item.getItemById(itemID), itemAmount, itemMeta))
    }
  }

  override def getDescPacket: PacketTile =
  {
    if (this.getStackInSlot(6) != null)
    {
      return new PacketTile(x.toInt, y.toInt, z.toInt, Array[Any](time, getStackInSlot(6)))
    }
    return new PacketTile(x.toInt, y.toInt, z.toInt, Array[Any](time, -1, -1, -1))
  }

  override def readFromNBT(nbt: NBTTagCompound)
  {
    super.readFromNBT(nbt)
    this.time = nbt.getInteger("smeltingTicks")
  }

  override def writeToNBT(nbt: NBTTagCompound)
  {
    super.writeToNBT(nbt)
    nbt.setInteger("smeltingTicks", this.time)
  }

  override def isItemValidForSlot(slotID: Int, itemStack: ItemStack): Boolean =
  {
    if (slotID == 6)
    {
      return true
    }
    return itemStack.getItem eq QuantumContent.itemDarkMatter
  }
}
