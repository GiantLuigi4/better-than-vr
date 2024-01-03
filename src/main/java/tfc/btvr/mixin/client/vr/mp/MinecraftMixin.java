package tfc.btvr.mixin.client.vr.mp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.net.handler.NetClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.itf.VRPlayerAttachments;
import tfc.btvr.lwjgl3.MPManager;
import tfc.btvr.mp.VRSuperPacket;
import tfc.btvr.mp.packets.MatricesPacket;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftMixin {
	@Shadow
	public abstract NetClientHandler getSendQueue();
	
	@Shadow
	public EntityPlayerSP thePlayer;
	
	@Inject(at = @At("TAIL"), method = "runTick")
	public void postTick(CallbackInfo ci) {
		if (thePlayer != null && getSendQueue() != null) {
			if (MPManager.modPresent) {
				if (((VRPlayerAttachments) thePlayer).better_than_vr$enabled())
					getSendQueue().addToSendQueue(new VRSuperPacket(new MatricesPacket(thePlayer)));
			}
		}
	}
}
