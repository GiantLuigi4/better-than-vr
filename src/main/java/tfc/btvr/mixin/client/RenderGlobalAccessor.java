package tfc.btvr.mixin.client;

import net.minecraft.client.render.RenderGlobal;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderGlobal.class)
public interface RenderGlobalAccessor {
	@Accessor
	World getWorldObj();
}
