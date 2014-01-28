package resonantinduction.core.resource.fluid;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import resonantinduction.core.Reference;
import resonantinduction.core.ResonantInduction;
import resonantinduction.core.Settings;
import resonantinduction.core.resource.ResourceGenerator;
import resonantinduction.core.resource.TileMaterial;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Fluid class uses for molten materials.
 * 
 * @author Calclavia
 * 
 */
public class BlockFluidMaterial extends BlockFluidFinite
{
	public BlockFluidMaterial(Fluid fluid)
	{
		super(Settings.CONFIGURATION.get(Configuration.CATEGORY_BLOCK, fluid.getName(), Settings.getNextBlockID()).getInt(), fluid, Material.lava);
		setTextureName(Reference.PREFIX + "molten_flow");
		setUnlocalizedName(Reference.PREFIX + "fluidMaterial");
		setQuantaPerBlock(16);
	}

	public void setQuanta(World world, int x, int y, int z, int quanta)
	{
		if (quanta > 0)
			world.setBlockMetadataWithNotify(x, y, z, quanta, 3);
		else
			world.setBlockToAir(x, y, z);
	}

	/* IFluidBlock */
	@Override
	public FluidStack drain(World world, int x, int y, int z, boolean doDrain)
	{
		FluidStack stack = new FluidStack(getFluid(), (int) (FluidContainerRegistry.BUCKET_VOLUME * this.getFilledPercentage(world, x, y, z)));
		if (doDrain)
			world.setBlockToAir(x, y, z);
		return stack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		return ResourceGenerator.materialColors.get(getFluid().getName().replace("molten", "").toLowerCase());
	}

	@Override
	public boolean canDrain(World world, int x, int y, int z)
	{
		return true;
	}
}