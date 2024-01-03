package tfc.btvr.mixin.client.vr.core;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.camera.ICamera;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.VRCamera;
import tfc.btvr.lwjgl3.BTVRSetup;

import java.nio.FloatBuffer;

@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {
	@Shadow
	private float farPlaneDistance;
	
	@Shadow
	private double cameraZoom;
	
	@Shadow
	private double cameraYaw;
	
	@Shadow
	private double cameraPitch;
	
	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
	
	private static float pct;
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/camera/ICamera;applyGlTransformations()V"), method = "orientCamera")
	public void postSetupTransform(ICamera instance) {
		if (!BTVRSetup.checkVR()) return;
	
		VRCamera.apply(pct, instance, farPlaneDistance);
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/camera/ICamera;applyGlTransformations()V", shift = At.Shift.AFTER), method = "orientCamera", cancellable = true)
	public void postSetupTransform(float renderPartialTicks, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;

//		if (Eye.getActiveEye() != null) {
		ci.cancel();
//		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "orientCamera")
	public void preSetupTransform(float renderPartialTicks, CallbackInfo ci) {
		pct = renderPartialTicks;
	}
}
