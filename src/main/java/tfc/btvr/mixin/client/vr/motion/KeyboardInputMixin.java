package tfc.btvr.mixin.client.vr.motion;

import net.minecraft.client.input.PlayerInput;
import net.minecraft.core.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.openvr.SVRControllerInput;

@Mixin(value = PlayerInput.class, remap = false)
public class KeyboardInputMixin {
	@Shadow public boolean jump;
	
	@Inject(at = @At("TAIL"), method = "tick")
	public void postTick(EntityPlayer entityplayer, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		jump = jump || SVRControllerInput.getInput("gameplay", "Jump");
	}
}
