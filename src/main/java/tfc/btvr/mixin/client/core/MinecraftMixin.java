package tfc.btvr.mixin.client.core;

import net.minecraft.client.GameResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.core.Timer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.VRSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.VRManager;
import tfc.btvr.lwjgl3.VRRenderManager;
import tfc.btvr.lwjgl3.openvr.Eye;

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
	@Final
	public GameResolution resolution;
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Renderer;endRenderGame(F)V", shift = At.Shift.AFTER), method = "run")
	public void postRender(CallbackInfo ci) {
		VRRenderManager.blitUI();
	}
	
	boolean alt = false;
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Renderer;beginRenderGame(F)V", shift = At.Shift.BEFORE), method = "run")
	public void preRender(CallbackInfo ci) {
		VRManager.tick();
		
		VRRenderManager.startFrame(resolution, (float) gameSettings.renderScale.value.scale, gameSettings.renderScale.value.useLinearFiltering);
		
		if (VRManager.inStandby) return; // no reason to render VR if the player's not in VR yet
		
		int rx = resolution.width;
		int ry = resolution.height;
		
		boolean rrw = VRSystem.VRSystem_ShouldApplicationReduceRenderingWork();
		
		// draw left
		if (!rrw || alt) {
			VRRenderManager.start(0);
			resolution.width = Eye.getActiveEye().width;
			resolution.height = Eye.getActiveEye().height;
			
			this.render.beginRenderGame(this.timer.partialTicks);
			
			GL11.glEnable(3008);
			if (!this.skipRenderWorld) {
				if (this.playerController != null) {
					this.playerController.setPartialTime(this.timer.partialTicks);
				}
				
				this.worldRenderer.updateCameraAndRender(this.timer.partialTicks);
			}
			
			this.render.endRenderGame(this.timer.partialTicks);
		}
		
		if (!rrw || !alt) {
			// draw right
			VRRenderManager.start(1);
			resolution.width = Eye.getActiveEye().width;
			resolution.height = Eye.getActiveEye().height;
			
			this.render.beginRenderGame(this.timer.partialTicks);
			GL11.glEnable(3008);
			if (!this.skipRenderWorld) {
				if (this.playerController != null) {
					this.playerController.setPartialTime(this.timer.partialTicks);
				}
				
				this.worldRenderer.updateCameraAndRender(this.timer.partialTicks);
			}
			
			this.render.endRenderGame(this.timer.partialTicks);
		}
		
		VRRenderManager.frameFinished(rrw, alt);
		alt = !alt;
		
		// reset to non-vr
		VRRenderManager.start(-1);
		
		resolution.width = rx;
		resolution.height = ry;
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;updateEntities()V"), method = "runTick")
	public void preTick(CallbackInfo ci) {
		VRManager.tickGame((Minecraft) (Object) this);
	}
}
