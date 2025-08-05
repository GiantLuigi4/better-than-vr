package tfc.btvr.mixin.client.access;

import net.minecraft.client.ScaledResolution;
import net.minecraft.client.render.window.GameWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ScaledResolution.class, remap = false)
public interface ResolutionAccessor {
	@Accessor("gameWindow") GameWindow getGameWindow();
	@Accessor("scaledWidth") void setScaledWidth(int value);
	@Accessor("scaledHeight") void setScaledHeight(int value);
	@Accessor("scaledWidthExact") void setScaledWidthExact(double value);
	@Accessor("scaledHeightExact") void setScaledHeightExact(double value);
}
