package tfc.btvr.mixin;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GLContext.class, remap = false)
public class ContextGLMixin {
	@Inject(at = @At("HEAD"), method = "useContext(Ljava/lang/Object;Z)V", cancellable = true)
	private static void preUseCtx(Object context, boolean forwardCompatible, CallbackInfo ci) {
		GL.createCapabilities();
		ci.cancel();
	}
}
