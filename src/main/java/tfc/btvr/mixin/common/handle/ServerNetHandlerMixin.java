package tfc.btvr.mixin.common.handle;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfc.btvr.itf.NetHandlerAccessor;

@Mixin(value = PacketHandlerServer.class, remap = false)
public class ServerNetHandlerMixin implements NetHandlerAccessor {
	@Shadow
	private PlayerServer playerEntity;
	
	@Shadow private MinecraftServer mcServer;
	
	@Override
	public Player better_than_vr$getPlayer() {
		return playerEntity;
	}
	
	@Override
	public boolean better_than_vr$isServer() {
		return true;
	}
	
	@Override
	public Entity better_than_vr$getEntity(int id) {
		WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
		return worldserver.getEntityFromId(id);
	}
}
