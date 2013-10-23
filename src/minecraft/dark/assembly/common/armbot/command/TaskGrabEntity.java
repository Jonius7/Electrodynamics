package dark.assembly.common.armbot.command;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import dark.api.al.coding.IArmbot;
import dark.api.al.coding.args.ArgumentData;
import dark.api.al.coding.args.ArgumentListData;
import dark.assembly.common.armbot.TaskBaseProcess;
import dark.core.prefab.helpers.EntityDictionary;

public class TaskGrabEntity extends TaskGrabPrefab
{
    /** If the grab command is specific to one entity this tell whether or not to grab the child
     * version of that entity. */
    public boolean child = false;
    /** The item to be collected. */
    private Class<? extends Entity> entityToInclude;

    @SuppressWarnings("unchecked")
    public TaskGrabEntity()
    {
        super("Grab-Entity");
        this.defautlArguments.add(new ArgumentData("child", false));
        this.defautlArguments.add(new ArgumentListData("Entity", Entity.class, EntityDictionary.getList().toArray(new Object[1])));
    }

    @Override
    public ProcessReturn onMethodCalled()
    {
        if (super.onMethodCalled() == ProcessReturn.CONTINUE)
        {
            this.entityToInclude = Entity.class;
            try
            {
                if (this.getArg("Entity") instanceof Class)
                {
                    this.entityToInclude = (Class<? extends Entity>) this.getArg("Entity");
                }
            }
            catch (Exception e)
            {

            }
            if (this.getArg("child") instanceof Boolean)
            {
                this.child = (Boolean) this.getArg("child");
            }

            return ProcessReturn.CONTINUE;
        }
        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        if (super.onUpdate() == ProcessReturn.CONTINUE)
        {
            if (((IArmbot) this.program.getMachine()).getGrabbedObject() != null)
            {
                return ProcessReturn.DONE;
            }
            @SuppressWarnings("unchecked")
            List<Entity> found = this.program.getMachine().getLocation().left().getEntitiesWithinAABB(entityToInclude, AxisAlignedBB.getBoundingBox(this.armPos.x - radius, this.armPos.y - radius, this.armPos.z - radius, this.armPos.x + radius, this.armPos.y + radius, this.armPos.z + radius));

            if (found != null && found.size() > 0)
            {
                for (Entity entity : found)
                {
                    if ((entity != null && !(entity instanceof EntityArrow) && !(entity instanceof EntityPlayer) && (!(entity instanceof EntityAgeable) || (entity instanceof EntityAgeable && child == ((EntityAgeable) entity).isChild()))))
                    {
                        ((IArmbot) this.program.getMachine()).grab(entity);
                        if (this.belt != null)
                        {
                            belt.ignoreEntity(entity);
                        }
                        return ProcessReturn.DONE;
                    }
                }
            }

            return ProcessReturn.CONTINUE;
        }
        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public TaskBaseProcess load(NBTTagCompound taskCompound)
    {
        super.loadProgress(taskCompound);
        this.child = taskCompound.getBoolean("child");
        this.entityToInclude = EntityDictionary.get(taskCompound.getString("name"));
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound taskCompound)
    {
        super.saveProgress(taskCompound);
        taskCompound.setBoolean("child", child);
        taskCompound.setString("name", ((this.entityToInclude != null) ? EntityDictionary.get(this.entityToInclude) : ""));
        return taskCompound;
    }

    @Override
    public String toString()
    {
        String baby = "";
        String entity = "";
        if (this.entityToInclude != null)
        {
            entity = EntityDictionary.get(this.entityToInclude);
            if (this.child)
            {
                // TODO do check for EntityAgable
                baby = "baby ";
            }
        }
        return "GRAB " + baby + entity;
    }

    @Override
    public TaskBaseProcess clone()
    {
        return new TaskGrabEntity();
    }

}
