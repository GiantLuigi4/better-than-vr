package tfc.btvr.mixin.client.safety;

import net.minecraft.core.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.itf.OSlider;

@Mixin(value = Entity.class, remap = false)
public class EntityMixin implements OSlider {
	@Shadow
	public float ySlideOffset;
	@Unique
	double oSlideOffset;
	
	@Inject(at = @At("HEAD"), method = "tick")
	public void preTick(CallbackInfo ci) {
		oSlideOffset = ySlideOffset;
	}
	
	@Override
	public double better_than_vr$getOSlide() {
		return oSlideOffset;
	}
}
