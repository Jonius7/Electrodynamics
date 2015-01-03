package resonantinduction.core.resource.content

import net.minecraft.item.{Item, ItemStack}
import resonantinduction.core.resource.ResourceFactory

/**
 * A class used by rubble, dusts and refined dusts
 * @author Calclavia
 */
abstract class ItemResource(material : String) extends Item
{
  override def getColorFromItemStack(p_82790_1_ : ItemStack, p_82790_2_ : Int): Int =
  {
    return ResourceFactory.getColor(material)
  }
}