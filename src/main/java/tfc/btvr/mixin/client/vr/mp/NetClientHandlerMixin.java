package tfc.btvr.mixin.client.vr.mp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.NetClientHandler;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfc.btvr.itf.NetHandlerAccessor;

@Mixin(NetClientHandler.class)
public abstract class NetClientHandlerMixin implements NetHandlerAccessor {
	@Shadow protected abstract Entity getEntityByID(int i);
	
	@Override
	public EntityPlayer better_than_vr$getPlayer() {
		Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
		return mc.thePlayer;
	}
	
	@Override
	public boolean better_than_vr$isServer() {
		return false;
	}
	
	@Override
	public Entity better_than_vr$getEntity(int id) {
		return getEntityByID(id);
	}
}
