package tfc.btvr.mixin.client;

import net.minecraft.client.gui.options.data.OptionsPages;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.Config;

@Mixin(value = OptionsPages.class, remap = false)
public class OptionMenuMixin {
	@Inject(at = @At("TAIL"), method = "<clinit>")
	private static void postInit(CallbackInfo ci) {
		Config.getVRPage();
	}
}
