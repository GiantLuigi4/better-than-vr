package tfc.btvr.mixin.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.ItemRenderer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.openvr.Eye;

@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {
	@Shadow
	private Minecraft mc;
	
	@Shadow
	public ItemRenderer itemRenderer;
	
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 2), method = "updateCameraAndRender", cancellable = true)
	public void preGetCurrentScreen(float renderPartialTicks, CallbackInfo ci) {
		// UIs are drawn specially for VR
		if (Eye.getActiveEye() != null) {
			ci.cancel();
		} else {
			VRRenderManager.grabUI(false);
		}
	}
	
	@Inject(at = @At(value = "RETURN"), method = "updateCameraAndRender")
	public void postGetCurrentScreen(float renderPartialTicks, CallbackInfo ci) {
		VRRenderManager.releaseUI();
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(FZII)V", shift = At.Shift.BEFORE), method = "updateCameraAndRender", cancellable = true)
	public void preRenderOverlay(float renderPartialTicks, CallbackInfo ci) {
		// UIs are drawn specially for VR
		if (Eye.getActiveEye() != null) {
			ci.cancel();
		} else {
			VRRenderManager.grabUI(true);
		}
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(FZII)V", shift = At.Shift.AFTER), method = "updateCameraAndRender")
	public void postRenderOverlay(float renderPartialTicks, CallbackInfo ci) {
		VRRenderManager.releaseUI();
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ItemRenderer;renderItemInFirstPerson(F)V"), method = "setupPlayerCamera")
	public void conditionallyRenderItem(ItemRenderer instance, float r) {
		// don't draw this in VR
//		instance.renderItemInFirstPerson(r);
	}
}
