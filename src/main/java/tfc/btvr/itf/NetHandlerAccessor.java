package tfc.btvr.itf;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;

public interface NetHandlerAccessor {
	EntityPlayer better_than_vr$getPlayer();
	
	boolean better_than_vr$isServer();
	
	Entity better_than_vr$getEntity(int id);
}
