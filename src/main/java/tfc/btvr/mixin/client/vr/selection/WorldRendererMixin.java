package tfc.btvr.mixin.client.vr.selection;

import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.core.util.phys.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.VRHelper;
import tfc.btvr.util.config.Config;

@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {
	@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/EntityPlayerSP;getPosition(F)Lnet/minecraft/core/util/phys/Vec3d;"))
	public Vec3d preGetPos(EntityPlayerSP instance, float v) {
		if (!BTVRSetup.checkVR()) return instance.getPosition(v);

		double[] oset = VRHelper.playerRelative(
				Config.TRACE_HAND.get()
		);
		return
				Vec3d.createVector(
						instance.x + oset[0],
						instance.bb.minY + oset[1],
						instance.z + oset[2]
				);
	}
	
	@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/EntityPlayerSP;getViewVector(F)Lnet/minecraft/core/util/phys/Vec3d;"))
	public Vec3d preGetRot(EntityPlayerSP instance, float v) {
		if (!BTVRSetup.checkVR()) return instance.getViewVector(v);
	
		double[] oset = VRHelper.getTraceVector(
				Config.TRACE_HAND.get()
		);
		return
				Vec3d.createVector(
						oset[0],
						oset[1],
						oset[2]
				);
	}
}
