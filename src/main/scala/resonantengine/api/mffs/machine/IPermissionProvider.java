package resonantengine.api.mffs.machine;

import com.mojang.authlib.GameProfile;
import resonantengine.lib.access.Permission;

public interface IPermissionProvider {
   boolean hasPermission(GameProfile var1, Permission var2);
}
