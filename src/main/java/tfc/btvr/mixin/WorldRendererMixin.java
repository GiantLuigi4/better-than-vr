package tfc.btvr.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.camera.ICamera;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
import org.lwjgl.util.glu.GLU;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.VRCamera;
import tfc.btvr.lwjgl3.openvr.Device;
import tfc.btvr.lwjgl3.openvr.DeviceType;
import tfc.btvr.lwjgl3.openvr.Eye;

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
		VRCamera.apply(pct, instance, farPlaneDistance);
	}
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/camera/ICamera;applyGlTransformations()V", shift = At.Shift.AFTER), method = "orientCamera", cancellable = true)
	public void postSetupTransform(float renderPartialTicks, CallbackInfo ci) {
//		if (Eye.getActiveEye() != null) {
		ci.cancel();
//		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "orientCamera")
	public void preSetupTransform(float renderPartialTicks, CallbackInfo ci) {
		pct = renderPartialTicks;
	}
}
