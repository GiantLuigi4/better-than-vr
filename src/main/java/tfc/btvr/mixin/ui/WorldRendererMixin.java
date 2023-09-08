package tfc.btvr.mixin.ui;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.openvr.Eye;

@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 2), method = "updateCameraAndRender", cancellable = true)
	public void preGetCurrentScreen(float renderPartialTicks, CallbackInfo ci) {
		// UIs are drawn specially for VR
		if (Eye.getActiveEye() != null) {
			ci.cancel();
		} else {
			VRRenderManager.grabUI();
		}
	}
	
	@Inject(at = @At(value = "RETURN"), method = "updateCameraAndRender")
	public void postGetCurrentScreen(float renderPartialTicks, CallbackInfo ci) {
		VRRenderManager.releaseUI();
	}
}
