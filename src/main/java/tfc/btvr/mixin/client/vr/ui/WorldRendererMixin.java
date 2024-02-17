package tfc.btvr.mixin.client.vr.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.input.PlayerInput;
import net.minecraft.client.player.controller.PlayerControllerSP;
import net.minecraft.client.render.*;
import net.minecraft.client.render.camera.EntityCameraFirstPerson;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.VRCamera;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.generic.Eye;
import tfc.btvr.lwjgl3.openvr.SEye;
import tfc.btvr.menu.MenuWorld;
import tfc.btvr.mixin.client.RenderGlobalAccessor;
import tfc.btvr.util.config.Config;
import tfc.btvr.util.config.MenuModeOption;

import static tfc.btvr.BTVR.menuWorld;

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
	public abstract void updateRenderer();
	
	@Shadow
	private FogManager fogManager;
	
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
		VRCamera.drawUI(mc, renderPartialTicks, menuWorld != null && mc.theWorld == menuWorld.dummy);
	}
	
	@Unique
	MenuModeOption.MenuMode last = Config.MENU_MODE.get();
	
	@Unique
	long frameMS = 0;
	
	@Unique
	float menuPct;
	
	@Inject(at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glMatrixMode(I)V", shift = At.Shift.AFTER, ordinal = 1), method = "updateCameraAndRender")
	public void preRender(float renderPartialTicks, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
		
		if (last != Config.MENU_MODE.get()) {
			last = Config.MENU_MODE.get();
			if (menuWorld != null)
				menuWorld.delete();
			menuWorld = null;
		}
		
		if (menuWorld == null && mc.theWorld != null) return;
		if (menuWorld != null && mc.theWorld != null) {
			menuWorld.delete();
			menuWorld = null;
			return;
		}
		
		if (mc.currentScreen == null) return;
		Eye eye = Eye.getActiveEye();
		// no point in drawing it if it won't be seen
		if (Config.HYBRID_MODE.get() && eye == null) return;
		
		GL11.glDepthMask(true);
		GL11.glDepthFunc(515);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		if (menuWorld == null || ((RenderGlobalAccessor) mc.renderGlobal).getWorldObj() != menuWorld.dummy) {
			if (menuWorld != null)
				menuWorld.delete();
			menuWorld = null;
		}
		if (menuWorld == null) menuWorld = MenuWorld.select(mc);
		
		RenderGlobal renderglobal = mc.renderGlobal;
		
		EntityPlayerSP tmpP = mc.thePlayer;
		mc.thePlayer = (EntityPlayerSP) menuWorld.myPlayer;
		World tmp = mc.theWorld;
		mc.theWorld = menuWorld.dummy;
		ICamera tmpC = mc.activeCamera;
		mc.activeCamera = new EntityCameraFirstPerson(mc, menuWorld.myPlayer);
		
		// tick player motion
		int tps = 20;
		int rate = 1000 / tps;
		if (Eye.getActiveEye().id == 0) {
			menuPct = (frameMS - System.currentTimeMillis()) / (float) rate;
			
			if (System.currentTimeMillis() > frameMS) {
				frameMS = System.currentTimeMillis() + rate;
				
				// ensure properties
				menuWorld.myPlayer.heightOffset = 1.62F;
				menuWorld.myPlayer.bbWidth = 0.6f;
				menuWorld.myPlayer.bbHeight = 1.8f;
				menuWorld.myPlayer.fallDistance = 0;
				
				mc.playerController = new PlayerControllerSP(mc);
				menuWorld.myPlayer.heal(20);
				((EntityPlayerSP) menuWorld.myPlayer).input = new PlayerInput(mc);
				((EntityPlayerSP) menuWorld.myPlayer).input.tick(menuWorld.myPlayer);
				menuWorld.myPlayer.tick();
				
				// constrain position
				if (menuWorld.myPlayer.x > menuWorld.sz) {
					menuWorld.myPlayer.x = menuWorld.sz;
					menuWorld.myPlayer.setPos(menuWorld.myPlayer.x, menuWorld.myPlayer.y, menuWorld.myPlayer.z);
				}
				if (menuWorld.myPlayer.x < -menuWorld.sz + 1) {
					menuWorld.myPlayer.x = -menuWorld.sz + 1;
					menuWorld.myPlayer.setPos(menuWorld.myPlayer.x, menuWorld.myPlayer.y, menuWorld.myPlayer.z);
				}
				if (menuWorld.myPlayer.z > menuWorld.sz) {
					menuWorld.myPlayer.z = menuWorld.sz;
					menuWorld.myPlayer.setPos(menuWorld.myPlayer.x, menuWorld.myPlayer.y, menuWorld.myPlayer.z);
				}
				if (menuWorld.myPlayer.z < -menuWorld.sz + 1) {
					menuWorld.myPlayer.z = -menuWorld.sz + 1;
					menuWorld.myPlayer.setPos(menuWorld.myPlayer.x, menuWorld.myPlayer.y, menuWorld.myPlayer.z);
				}
				if (menuWorld.myPlayer.y < -menuWorld.sz) {
					menuWorld.myPlayer.moveTo(0.5f, menuWorld.sz * 2, 0.5f, menuWorld.myPlayer.yRot, menuWorld.myPlayer.xRot);
				}
				
				menuPct = 1;
			}
			
			updateRenderer();
		}
		
		farPlaneDistance = menuWorld.sz - 2;
		
		VRCamera.apply(menuPct, null, 128);
		
		fogManager.updateFogColor(0);
		fogManager.setupFog(-1, farPlaneDistance, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		renderglobal.drawSky(0);
		fogManager.setupFog(0, farPlaneDistance, 0);
		GL11.glEnable(2912);
		Lighting.disable();
		
		mc.activeCamera = tmpC;
		mc.theWorld = tmp;
		mc.thePlayer = tmpP;
		
		menuWorld.draw(renderPartialTicks, mc);
		
		VRCamera.renderPlayer(true, menuWorld.myPlayer, renderPartialTicks, mc.renderGlobal);
		VRCamera.drawUI(mc, renderPartialTicks, mc.theWorld == null || mc.theWorld == menuWorld.dummy);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
}
