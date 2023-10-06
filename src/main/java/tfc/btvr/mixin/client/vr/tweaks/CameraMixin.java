package tfc.btvr.mixin.client.vr.tweaks;

import net.minecraft.client.render.camera.EntityCamera;
import net.minecraft.core.entity.EntityLiving;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.lwjgl3.openvr.SDevice;
import tfc.btvr.math.MathHelper;

@Mixin(value = EntityCamera.class, remap = false)
public class CameraMixin {
	@Shadow
	@Final
	public EntityLiving entity;
	
	@Unique
	double oxr, xr;
	@Unique
	double oyr, yr;
	
	@Inject(at = @At("TAIL"), method = "tick")
	public void postTick(CallbackInfo ci) {
		double[] pTarget = VRHelper.getTraceVector(SDevice.HEAD);
		double d0 = pTarget[0];
		double d1 = pTarget[1];
		double d2 = pTarget[2];
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		
		oxr = xr;
		oyr = yr;
		this.xr = (MathHelper.wrapDegrees((float) (-(MathHelper.atan2(d1, d3) * (double) (180F / (float) Math.PI)))));
		this.yr = (MathHelper.wrapDegrees((float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F));
	}
	
	/**
	 * @author GiantLuigi4
	 * @reason fix particles
	 */
	@Overwrite
	public double getXRot(float renderPartialTicks) {
		return xr * renderPartialTicks + oxr * (1 - renderPartialTicks);
	}
	
	/**
	 * @author GiantLuigi4
	 * @reason fix particles
	 */
	@Overwrite
	public double getYRot(float renderPartialTicks) {
		return yr * renderPartialTicks + oyr * (1 - renderPartialTicks);
	}
}
