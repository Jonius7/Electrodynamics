package liquidmechanics.core.pressure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import liquidmechanics.core.implement.IPipe;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.FMLLog;

/**
 * based on Calclavia's UE Electric Network stuff
 * 
 */
public class FluidPressure
{
    public static FluidPressure instance = new FluidPressure();

    private List<FluidPressureNetwork> electricityNetworks = new ArrayList<FluidPressureNetwork>();

    /**
     * Registers a conductor into the UE electricity net.
     */
    public void registerConductor(IPipe newConductor)
    {
        this.cleanUpNetworks();
        FluidPressureNetwork newNetwork = new FluidPressureNetwork(newConductor);
        this.electricityNetworks.add(newNetwork);
    }

    public void unregister(TileEntity tileEntity)
    {
        for (FluidPressureNetwork network : this.electricityNetworks)
        {
            network.stopProducing(tileEntity);
            network.stopRequesting(tileEntity);
        }
    }

    /**
     * Merges two connection lines together into one.
     * 
     * @param networkA
     *            - The network to be merged into. This network will be kept.
     * @param networkB
     *            - The network to be merged. This network will be deleted.
     */
    public void mergeConnection(FluidPressureNetwork networkA, FluidPressureNetwork networkB)
    {
        if (networkA != networkB)
        {
            if (networkA != null && networkB != null)
            {
                networkA.conductors.addAll(networkB.conductors);
                networkA.setNetwork();
                this.electricityNetworks.remove(networkB);
                networkB = null;

                networkA.cleanConductors();
            }
            else
            {
                System.err.println("Failed to merge Universal Electricity wire connections!");
            }
        }
    }

    /**
     * Separate one connection line into two different ones between two
     * conductors. This function does this by resetting all wires in the
     * connection line and making them each reconnect.
     * 
     * @param conductorA
     *            - existing conductor
     * @param conductorB
     *            - broken/invalid conductor
     */
    public void splitConnection(IPipe conductorA, IPipe conductorB)
    {
        try
        {
            FluidPressureNetwork network = conductorA.getNetwork();

            if (network != null)
            {
                network.cleanConductors();
                network.resetConductors();

                Iterator it = network.conductors.iterator();

                while (it.hasNext())
                {
                    IPipe conductor = (IPipe) it.next();

                    for (byte i = 0; i < 6; i++)
                    {
                        conductor.updateConnectionWithoutSplit(Vector3.getConnectorFromSide(((TileEntity) conductor).worldObj, new Vector3((TileEntity) conductor), ForgeDirection.getOrientation(i)), ForgeDirection.getOrientation(i));
                    }
                }
            }
            else
            {
                FMLLog.severe("Conductor invalid network while splitting connection!");
            }
        }
        catch (Exception e)
        {
            FMLLog.severe("Failed to split wire connection!");
            e.printStackTrace();
        }
    }

    /**
     * Clean up and remove all useless and invalid connections.
     */
    public void cleanUpNetworks()
    {
        try
        {
            Iterator it = electricityNetworks.iterator();

            while (it.hasNext())
            {
                FluidPressureNetwork network = (FluidPressureNetwork) it.next();
                network.cleanConductors();

                if (network.conductors.size() == 0)
                {
                    it.remove();
                }
            }
        }
        catch (Exception e)
        {
            FMLLog.severe("Failed to clean up wire connections!");
            e.printStackTrace();
        }
    }

    public void resetConductors()
    {
        Iterator it = electricityNetworks.iterator();

        while (it.hasNext())
        {
            FluidPressureNetwork network = ((FluidPressureNetwork) it.next());
            network.resetConductors();
        }
    }
}
