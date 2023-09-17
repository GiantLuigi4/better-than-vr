package tfc.btvr.mixin.client.vr.display;

import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.openvr.Eye;

@Mixin(value = GL30.class, remap = false)
public class GL30Mixin {
	@Inject(at = @At("HEAD"), method = "glBindFramebuffer", cancellable = true)
	private static void preBind(int target, int framebuffer, CallbackInfo ci) {
		if (framebuffer == 0) {
			Eye active = Eye.getActiveEye();
			if (active != null) {
				GL30.glBindFramebuffer(target, active.fboId());
				ci.cancel();
			} else if (VRRenderManager.isGrabbingUI()) {
				VRRenderManager.grabUI();
			}
		}
	}
}
