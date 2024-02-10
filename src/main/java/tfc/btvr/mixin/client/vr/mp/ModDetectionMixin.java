package tfc.btvr.mixin.client.vr.mp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.NetClientHandler;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.Packet3Chat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.MPManager;

@Mixin(value = NetClientHandler.class, remap = false)
public abstract class ModDetectionMixin {
	@Shadow
	public abstract void addToSendQueue(Packet packet);
	
	@Inject(at = @At("RETURN"), method = "<init>")
	public void postInit(Minecraft minecraft, String s, int i, CallbackInfo ci) {
		MPManager.modPresent = false;
	}
	
	@Inject(at = @At("HEAD"), method = "handleChat")
	public void onReceiveChat(Packet3Chat packet3chat, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		if (packet3chat.message.equals(
				MPManager.ackServerMsg
		)) {
			MPManager.modPresent = true;
			addToSendQueue(new Packet3Chat(
					MPManager.ackClientMsg
			));
		}
	}
}
