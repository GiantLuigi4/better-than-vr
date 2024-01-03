package tfc.btvr.mixin.common.handle;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfc.btvr.itf.NetHandlerAccessor;

@Mixin(NetServerHandler.class)
public class ServerNetHandlerMixin implements NetHandlerAccessor {
	@Shadow
	private EntityPlayerMP playerEntity;
	
	@Shadow private MinecraftServer mcServer;
	
	@Override
	public EntityPlayer better_than_vr$getPlayer() {
		return playerEntity;
	}
	
	@Override
	public boolean better_than_vr$isServer() {
		return true;
	}
	
	@Override
	public Entity better_than_vr$getEntity(int id) {
		WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
		return worldserver.func_6158_a(id);
	}
}
