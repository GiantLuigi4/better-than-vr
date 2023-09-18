package tfc.btvr.mixin.client.vr.selection;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Minecraft.class, remap = false)
public interface MinecraftAccessor {
	@Invoker
	void invokeClickMouse(int clickType, boolean attack, boolean repeat);
}
