package tfc.btvr.mixin.client.vr.motion;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.core.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.openvr.SVRControllerInput;

@Mixin(value = KeyboardInput.class, remap = false)
public class KeyboardInputMixin {
	@Inject(at = @At("TAIL"), method = "tick")
	public void postTick(EntityPlayer entityplayer, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		Input self = ((Input) (Object) this);
		self.jump = self.jump || SVRControllerInput.getInput("gameplay", "Jump");
	}
}
