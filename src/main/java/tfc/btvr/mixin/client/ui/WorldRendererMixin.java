package tfc.btvr.mixin.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.ItemRenderer;
import net.minecraft.client.render.WorldRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.Config;
import tfc.btvr.VRCamera;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.openvr.Eye;
import tfc.btvr.menu.MenuWorld;

@Mixin(value = WorldRenderer.class, remap = false)
public abstract class WorldRendererMixin {
	@Shadow
	private Minecraft mc;
	
	@Shadow
	public ItemRenderer itemRenderer;
	
	@Shadow
	private long systemTime;
	
	@Shadow
	public abstract void setupScaledResolution();

//	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;skipRenderWorld:Z"), method = "updateCameraAndRender", cancellable = true)
//	public void preRender(float renderPartialTicks, CallbackInfo ci) {
//		VRRenderManager.grabUI(false);
//		GL11.glViewport(0, 0, this.mc.resolution.width, this.mc.resolution.height);
//		GL11.glClear(256);
//
//		if (mc.theWorld == null) {
//			GL11.glMatrixMode(5889);
//			GL11.glLoadIdentity();
//			GL11.glMatrixMode(5888);
//			GL11.glLoadIdentity();
//			this.setupScaledResolution();
//
//			this.systemTime = System.nanoTime();
//		}
//
//		int width = this.mc.resolution.scaledWidth;
//		int height = this.mc.resolution.scaledHeight;
//		int mouseX = Mouse.getX() * width / this.mc.resolution.width;
//		int mouseY = height - Mouse.getY() * height / this.mc.resolution.height - 1;
//
//		if (this.mc.currentScreen != null) {
//			GL11.glClear(256);
//			if (this.mc.inputType == InputType.CONTROLLER) {
//				mouseX = (int)this.mc.controllerInput.cursorX;
//				mouseY = (int)this.mc.controllerInput.cursorY;
//			}
//
//			this.mc.currentScreen.drawScreen(mouseX, mouseY, renderPartialTicks);
//			if (this.mc.inputType == InputType.CONTROLLER && this.mc.currentScreen != null) {
//				this.mc.currentScreen.drawCursor();
//			}
//
//			if (this.mc.currentScreen != null && this.mc.currentScreen.particleRenderer != null) {
//				this.mc.currentScreen.particleRenderer.render(renderPartialTicks);
//			}
//		}
//		VRRenderManager.releaseUI();
//	}
	
	@Shadow
	private float farPlaneDistance;
	
	@Shadow
	private long prevFrameTime;
	
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 2), method = "updateCameraAndRender", cancellable = true)
	public void preGetCurrentScreen(float renderPartialTicks, CallbackInfo ci) {
		// UIs are drawn specially for VR
		if (Eye.getActiveEye() != null) {
			ci.cancel();
		} else {
			VRRenderManager.grabUI(false);
		}
	}
	
	@Inject(at = @At("RETURN"), method = "updateCameraAndRender")
	public void postRenderWorld(float renderPartialTicks, CallbackInfo ci) {
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
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderGlobal;renderEntities(Lnet/minecraft/client/render/camera/ICamera;F)V", shift = At.Shift.AFTER), method = "renderWorld")
	public void postRenderEnts(float renderPartialTicks, long updateRenderersUntil, CallbackInfo ci) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		if (mc.currentScreen == null) return;
		VRCamera.drawUI(mc, renderPartialTicks);
	}
	
	MenuWorld menuWorld = MenuWorld.select();
	
	@Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glMatrixMode(I)V", shift = At.Shift.AFTER), method = "updateCameraAndRender")
	public void preRender(float renderPartialTicks, CallbackInfo ci) {
		if (mc.currentScreen == null) return;
		Eye eye = Eye.getActiveEye();
		// no point in drawing it if it won't be seen
		if (Config.HYBRID_MODE.get() && eye == null) return;
		
		GL11.glDepthMask(true);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		VRCamera.apply(renderPartialTicks, null, 128);
		
		menuWorld.draw(renderPartialTicks, mc);
		
		VRCamera.renderPlayer(null, renderPartialTicks, mc.renderGlobal);
		VRCamera.drawUI(mc, renderPartialTicks);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
}