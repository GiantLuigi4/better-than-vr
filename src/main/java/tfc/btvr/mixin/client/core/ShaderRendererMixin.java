package tfc.btvr.mixin.client.core;

import net.minecraft.client.render.shader.ShadersRenderer;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.openvr.Eye;

import java.io.PrintStream;

@Mixin(value = ShadersRenderer.class, remap = false)
public class ShaderRendererMixin {
	@Inject(at = @At("HEAD"), method = "setupFramebuffer")
	public void preSetupFBO(CallbackInfo ci) {
		if (Eye.getActiveEye() != null) {
			Eye active = Eye.getActiveEye();
			
			((ShadersRenderer) (Object) this).mc.resolution.width = active.width;
			((ShadersRenderer) (Object) this).mc.resolution.height = active.height;
		} else {
			((ShadersRenderer) (Object) this).mc.resolution.width = Display.getWidth();
			((ShadersRenderer) (Object) this).mc.resolution.height = Display.getHeight();
		}
	}
	
	@Redirect(method = "setupFramebuffer", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"))
	public void doNotPrint(PrintStream instance, String x) {
	}
}
