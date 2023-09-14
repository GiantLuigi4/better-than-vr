package tfc.btvr.mixin.client.selection;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
	@Invoker
	void invokeClickMouse(int clickType, boolean attack, boolean repeat);
}
