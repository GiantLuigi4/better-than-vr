package tfc.btvr.mixin.client.vr.ui;

import net.minecraft.client.option.ImmersiveModeOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.btvr.lwjgl3.BTVRSetup;

@Mixin(value = ImmersiveModeOption.class, remap = false)
public class DisableCrosshairMixin {
	@Inject(at = @At("HEAD"), method = "drawCrosshair", cancellable = true)
	public void redir(CallbackInfoReturnable<Boolean> cir) {
		if (!BTVRSetup.checkVR()) return;
		
		cir.setReturnValue(false);
	}
}
