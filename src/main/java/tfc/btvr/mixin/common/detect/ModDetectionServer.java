package tfc.btvr.mixin.common.detect;

import net.minecraft.core.net.packet.Packet3Chat;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.itf.VRPlayerAttachments;
import tfc.btvr.lwjgl3.MPManager;

@Mixin(value = NetServerHandler.class, remap = false)
public class ModDetectionServer {
	@Shadow private EntityPlayerMP playerEntity;
	
	@Inject(at = @At("HEAD"), method = "handleChat", cancellable = true)
	public void postChat(Packet3Chat packet, CallbackInfo ci) {
		if (packet.message.equals(MPManager.ackClientMsg)) {
			((VRPlayerAttachments) playerEntity).better_than_vr$setEnabled(true);
			
			ci.cancel();
		}
	}
}
