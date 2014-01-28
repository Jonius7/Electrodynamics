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
public class BlockFluidMaterial extends BlockFluidFinite implements ITileEntityProvider
{
	public BlockFluidMaterial()
	{
		super(Settings.CONFIGURATION.get(Configuration.CATEGORY_BLOCK, "fluidMaterial", Settings.getNextBlockID()).getInt(), ResonantInduction.fluidMaterial, Material.lava);
		setTextureName(Reference.PREFIX + "molten_flow");
		this.setUnlocalizedName(Reference.PREFIX + "fluidMaterial");
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
		TileMaterial tileFluid = (TileMaterial) world.getBlockTileEntity(x, y, z);
		FluidStack stack = new FluidStack(ResonantInduction.fluidMaterial, (int) (FluidContainerRegistry.BUCKET_VOLUME * this.getFilledPercentage(world, x, y, z)));
		return stack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
	{
		TileEntity tileEntity = access.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileMaterial)
		{
			return ((TileMaterial) tileEntity).clientColor;
		}

		return 16777215;
	}

	@Override
	public boolean canDrain(World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileMaterial();
	}
}
