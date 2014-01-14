package resonantinduction.mechanical.fluid.pipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import resonantinduction.core.Reference;
import resonantinduction.mechanical.Mechanical;
import resonantinduction.mechanical.fluid.valve.ModelReleaseValve;
import resonantinduction.mechanical.fluid.valve.RenderReleaseValve;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemPipeRenderer implements IItemRenderer
{
	private ModelReleaseValve valve = new ModelReleaseValve();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		GL11.glPushMatrix();
		GL11.glRotatef(180f, 0f, 0f, 1f);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderPipe.getTexture(item.getItemDamage()));
		int meta = item.getItemDamage();
		
		if (Mechanical.blockReleaseValve != null && item.itemID == Mechanical.blockReleaseValve.blockID)
		{
			meta = FluidContainerMaterial.IRON.getMeta();
		}
		if (type == ItemRenderType.ENTITY)
		{
			GL11.glTranslatef(-.5F, -1F, -.5F);
			RenderPipe.render(meta, (byte) 0b001100);
		}
		else if (type == ItemRenderType.INVENTORY)
		{
			GL11.glTranslatef(0F, -1F, 0F);
			RenderPipe.render(meta, (byte) 0b001100);
		}
		else if (type == ItemRenderType.EQUIPPED)
		{
			GL11.glTranslatef(-1F, -1.2F, 0.5F);
			RenderPipe.render(meta, (byte) 0b000011);
		}
		else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
		{
			GL11.glTranslatef(-2F, -1.5F, 0.2F);
			RenderPipe.render(meta, (byte) 0b000011);
		}
		else
		{
			RenderPipe.render(item.getItemDamage(), (byte) 0b000011);
		}
		if (Mechanical.blockReleaseValve != null && item.itemID == Mechanical.blockReleaseValve.blockID)
		{
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderReleaseValve.VALVE_TEXTURE);
			valve.render();
		}
		
		GL11.glPopMatrix();
	}
}
