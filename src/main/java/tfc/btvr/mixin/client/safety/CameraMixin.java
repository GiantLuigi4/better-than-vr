package tfc.btvr.mixin.client.safety;

import net.minecraft.client.render.camera.EntityCamera;
import net.minecraft.core.entity.EntityLiving;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.btvr.itf.OSlider;

@Mixin(value = EntityCamera.class, remap = false)
public class CameraMixin {
	@Shadow @Final public EntityLiving entity;
	
	@Inject(at = @At("RETURN"), method = "getY", cancellable = true)
	public void modulateY(float renderPartialTicks, CallbackInfoReturnable<Double> cir) {
		cir.setReturnValue(
				cir.getReturnValueD() +
						entity.ySlideOffset * renderPartialTicks +
						((OSlider)entity).better_than_vr$getOSlide() * (1 - renderPartialTicks)
		);
	}
}