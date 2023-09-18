package tfc.btvr.mixin.client.vr.tweaks.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.core.HitResult;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.itf.VRController;

@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {
	@Shadow
	private Minecraft mc;
	
	@Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderGlobal;drawBlockBreaking(Lnet/minecraft/client/render/camera/ICamera;Lnet/minecraft/core/HitResult;F)V"))
	public void redirBreaking(RenderGlobal instance, ICamera k, HitResult block, float x) {
		HitResult res = ((VRController) mc.playerController).better_than_vr$getResult();
		instance.drawBlockBreaking(k, res == null ? block : res, x);
	}
	
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;objectMouseOver:Lnet/minecraft/core/HitResult;", ordinal = 0), method = "renderWorld")
	public void preOMouseOver(float renderPartialTicks, long updateRenderersUntil, CallbackInfo ci) {
		if (mc.objectMouseOver == null) {
			HitResult res = ((VRController) mc.playerController).better_than_vr$getResult();
			if (res != null) {
				GL11.glDisable(3008);
				mc.renderGlobal.drawBlockBreaking(this.mc.activeCamera, res, renderPartialTicks);
				GL11.glDisable(3008);
			}
		}
	}
}
