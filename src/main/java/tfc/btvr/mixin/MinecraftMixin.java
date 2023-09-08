package tfc.btvr.mixin;

import net.minecraft.client.GameResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.core.Timer;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.VRManager;
import tfc.btvr.lwjgl3.VRRenderManager;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftMixin {
	@Shadow
	public Renderer render;
	
	@Shadow
	private Timer timer;
	
	@Shadow
	public boolean skipRenderWorld;
	
	@Shadow
	public PlayerController playerController;
	
	@Shadow
	public WorldRenderer worldRenderer;
	
	@Shadow
	public GameSettings gameSettings;
	
	@Shadow
	public abstract void displayInGameMenu();
	
	@Shadow
	private long lastFocusTime;
	
	@Shadow
	public SoundManager sndManager;
	
	@Shadow
	protected abstract void drawFrameTimeGraph(long frameTime);
	
	@Shadow
	private long prevFrameTime;
	
	@Shadow
	@Final
	public GameResolution resolution;
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Renderer;endRenderGame(F)V", shift = At.Shift.AFTER), method = "run")
	public void postRender(CallbackInfo ci) {
		VRRenderManager.blitUI();
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Renderer;beginRenderGame(F)V", shift = At.Shift.BEFORE), method = "run")
	public void preRender(CallbackInfo ci) {
		VRManager.tick();
		
		VRRenderManager.startFrame(resolution, (float) gameSettings.renderScale.value.scale, gameSettings.renderScale.value.useLinearFiltering);
		
		// draw left
		VRRenderManager.start(0);
		
		this.render.beginRenderGame(this.timer.partialTicks);
		
		GL11.glEnable(3008);
		if (!this.skipRenderWorld) {
			if (this.playerController != null) {
				this.playerController.setPartialTime(this.timer.partialTicks);
			}
			
			this.worldRenderer.updateCameraAndRender(this.timer.partialTicks);
		}
		
		this.render.endRenderGame(this.timer.partialTicks);
		
		// draw right
		VRRenderManager.start(1);
		
		this.render.beginRenderGame(this.timer.partialTicks);
		
		GL11.glEnable(3008);
		if (!this.skipRenderWorld) {
			if (this.playerController != null) {
				this.playerController.setPartialTime(this.timer.partialTicks);
			}
			
			this.worldRenderer.updateCameraAndRender(this.timer.partialTicks);
		}
		
		this.render.endRenderGame(this.timer.partialTicks);
		
		VRRenderManager.frameFinished();
		
		// reset to non-vr
		VRRenderManager.start(-1);
	}
}
