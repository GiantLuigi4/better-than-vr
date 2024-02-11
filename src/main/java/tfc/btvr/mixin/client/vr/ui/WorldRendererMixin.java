package tfc.btvr.mixin.client.vr.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.render.ItemRenderer;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.camera.EntityCameraFirstPerson;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.Config;
import tfc.btvr.VRCamera;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.generic.Eye;
import tfc.btvr.lwjgl3.openvr.SEye;
import tfc.btvr.menu.MenuWorld;
import tfc.btvr.mixin.client.RenderGlobalAccessor;

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
	
	@Shadow
	protected abstract void updateFogColor(float renderPartialTicks);
	
	@Shadow
	protected abstract void setupFog(int i, float renderPartialTicks);
	
	@Shadow
	public abstract void updateRenderer();
	
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 2), method = "updateCameraAndRender", cancellable = true)
	public void preGetCurrentScreen(float renderPartialTicks, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		// UIs are drawn specially for VR
		if (SEye.getActiveEye() != null) {
			ci.cancel();
		} else {
			VRRenderManager.grabUI(false);
		}
		GL11.glDepthMask(true);
	}
	
	@Inject(at = @At("RETURN"), method = "updateCameraAndRender")
	public void postRenderWorld(float renderPartialTicks, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		VRRenderManager.releaseUI();
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(FZII)V", shift = At.Shift.BEFORE), method = "updateCameraAndRender", cancellable = true)
	public void preRenderOverlay(float renderPartialTicks, CallbackInfo ci) {
		// UIs are drawn specially for VR
		if (SEye.getActiveEye() != null) {
			ci.cancel();
		}
//		else {
//			VRRenderManager.grabUI(true);
//		}
//		GL11.glDepthMask(true);
	}

//	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(FZII)V", shift = At.Shift.AFTER), method = "updateCameraAndRender")
//	public void postRenderOverlay(float renderPartialTicks, CallbackInfo ci) {
//		VRRenderManager.releaseUI();
//	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/ItemRenderer;renderItemInFirstPerson(F)V"), method = "setupPlayerCamera")
	public void conditionallyRenderItem(ItemRenderer instance, float r) {
		if (BTVRSetup.checkVR()) return;
		
		// don't draw this in VR
		instance.renderItemInFirstPerson(r);
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderGlobal;renderEntities(Lnet/minecraft/client/render/camera/ICamera;F)V", shift = At.Shift.AFTER), method = "renderWorld")
	public void postRenderEnts(float renderPartialTicks, long updateRenderersUntil, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		if (mc.currentScreen == null) return;
		VRCamera.drawUI(mc, renderPartialTicks);
	}
	
	MenuWorld menuWorld;
	
	@Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glMatrixMode(I)V", shift = At.Shift.AFTER), method = "updateCameraAndRender")
	public void preRender(float renderPartialTicks, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		if (mc.currentScreen == null) return;
		Eye eye = Eye.getActiveEye();
		// no point in drawing it if it won't be seen
		if (Config.HYBRID_MODE.get() && eye == null) return;
		
		GL11.glDepthMask(true);
		GL11.glDepthFunc(515);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		VRCamera.apply(renderPartialTicks, null, 128);
		
		if (menuWorld == null || ((RenderGlobalAccessor) mc.renderGlobal).getWorldObj() != menuWorld.dummy)
			menuWorld = null;
		if (menuWorld == null) menuWorld = MenuWorld.select(mc);
		
		RenderGlobal renderglobal = mc.renderGlobal;
		
		EntityPlayerSP tmpP = mc.thePlayer;
		mc.thePlayer = (EntityPlayerSP) menuWorld.myPlayer;
		World tmp = mc.theWorld;
		mc.theWorld = menuWorld.dummy;
		ICamera tmpC = mc.activeCamera;
		mc.activeCamera = new EntityCameraFirstPerson(mc, menuWorld.myPlayer);
		
		updateRenderer();
		
		farPlaneDistance = 60f;
		
		updateFogColor(0);
		setupFog(-1, 0);
		renderglobal.drawSky(0);
		this.setupFog(0, 0);
		GL11.glEnable(2912);
		Lighting.disable();
		
		mc.activeCamera = tmpC;
		mc.theWorld = tmp;
		mc.thePlayer = tmpP;
		
		menuWorld.draw(renderPartialTicks, mc);
		
		VRCamera.renderPlayer(true, menuWorld.myPlayer, renderPartialTicks, mc.renderGlobal);
		VRCamera.drawUI(mc, renderPartialTicks);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
}
