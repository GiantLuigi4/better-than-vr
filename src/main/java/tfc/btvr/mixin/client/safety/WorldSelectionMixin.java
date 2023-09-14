package tfc.btvr.mixin.client.safety;

import net.minecraft.client.gui.GuiSelectWorld;
import org.lwjgl.openvr.VRCompositor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiSelectWorld.class, remap = false)
public class WorldSelectionMixin {
	@Inject(at = @At("HEAD"), method = "selectWorld")
	public void preSelect(int i, CallbackInfo ci) {
		// without this, SteamVR ends up interpolating between two frames weirdly and it creates a strobing image
		VRCompositor.VRCompositor_SuspendRendering(true);
	}
	
	@Inject(at = @At("RETURN"), method = "selectWorld")
	public void postSelect(int i, CallbackInfo ci) {
		VRCompositor.VRCompositor_SuspendRendering(false);
	}
}
