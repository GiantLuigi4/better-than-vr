package tfc.btvr.mixin.client.vr.ui;

import net.minecraft.client.GameResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.input.InputType;
import net.minecraft.client.input.controller.ControllerInput;
import net.minecraft.core.Timer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.VRRenderManager;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@Shadow
	public GuiScreen currentScreen;
	
	@Shadow
	@Final
	public GameResolution resolution;
	
	@Shadow
	public InputType inputType;
	
	@Shadow
	private Timer timer;
	
	@Shadow
	public ControllerInput controllerInput;
	
	@Shadow
	public GuiIngame ingameGUI;
	
	@Shadow
	public EntityPlayerSP thePlayer;
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Renderer;beginRenderGame(F)V", shift = At.Shift.BEFORE), method = "run")
	public void preRender(CallbackInfo ci) {
//		VRRenderManager.grabUI(false);
//
//		int width = resolution.scaledWidth;
//		int height = resolution.scaledHeight;
//
//		int mouseX = Mouse.getX() * width / resolution.width;
//		int mouseY = height - Mouse.getY() * height / resolution.height - 1;
//
//		if (currentScreen != null) {
//			GL11.glClear(256);
//			if (inputType == InputType.CONTROLLER) {
//				mouseX = (int) controllerInput.cursorX;
//				mouseY = (int) controllerInput.cursorY;
//			}
//
//			currentScreen.drawScreen(mouseX, mouseY, this.timer.partialTicks);
//			if (inputType == InputType.CONTROLLER && currentScreen != null) {
//				currentScreen.drawCursor();
//			}
//
//			if (currentScreen != null && currentScreen.particleRenderer != null) {
//				currentScreen.particleRenderer.render(this.timer.partialTicks);
//			}
//		}
//
//		VRRenderManager.releaseUI();
		
		VRRenderManager.grabUI(true);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		
		if (this.thePlayer != null) {
			int w = resolution.width;
			int h = resolution.height;
			int sw = resolution.scaledWidth;
			int sh = resolution.scaledHeight;
			double swe = resolution.scaledWidthExact;
			double she = resolution.scaledHeightExact;
			
			resolution.scaledWidthExact = resolution.width = resolution.scaledWidth = 960;
			resolution.scaledHeightExact = resolution.height = resolution.scaledHeight = resolution.scaledWidth / 2;
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			GL11.glColorMask(true, true, true, true);
			GL11.glEnable(2929);
			GL11.glEnable(3008);
			GL11.glViewport(0, 0, resolution.width, resolution.height);
			ingameGUI.renderGameOverlay(this.timer.partialTicks, currentScreen != null, Integer.MIN_VALUE, Integer.MIN_VALUE);
			
			resolution.width = w;
			resolution.height = h;
			resolution.scaledWidth = sw;
			resolution.scaledHeight = sh;
			resolution.scaledWidthExact = swe;
			resolution.scaledHeightExact = she;
		}
		
		GL11.glPopMatrix();
		GL11.glViewport(0, 0, resolution.scaledWidth, resolution.scaledHeight);
		VRRenderManager.releaseUI();
	}
}
