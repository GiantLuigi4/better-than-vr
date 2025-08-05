package tfc.btvr.mixin.client;

import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.world.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RenderGlobal.class, remap = false)
public interface RenderGlobalAccessor {
	@Accessor
	WorldClient getWorldObj();
}
