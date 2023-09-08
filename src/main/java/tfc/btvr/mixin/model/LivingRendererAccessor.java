package tfc.btvr.mixin.model;

import net.minecraft.client.render.entity.LivingRenderer;
import net.minecraft.client.render.model.ModelBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = LivingRenderer.class, remap = false)
public interface LivingRendererAccessor {
	@Accessor("mainModel")
	ModelBase getMainModel();
}
