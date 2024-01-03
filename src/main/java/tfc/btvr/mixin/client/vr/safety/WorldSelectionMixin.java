package tfc.btvr.mixin.client.vr.safety;

import net.minecraft.client.gui.GuiSelectWorld;
import org.lwjgl.openvr.VRCompositor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.BTVRSetup;

@Mixin(value = GuiSelectWorld.class, remap = false)
public class WorldSelectionMixin {
	@Inject(at = @At("HEAD"), method = "selectWorld")
	public void preSelect(int i, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
	
		// without this, SteamVR ends up interpolating between two frames weirdly and it creates a strobing image
		VRCompositor.VRCompositor_SuspendRendering(true);
	}
	
	@Inject(at = @At("RETURN"), method = "selectWorld")
	public void postSelect(int i, CallbackInfo ci) {
		if (!BTVRSetup.checkVR()) return;
	
		VRCompositor.VRCompositor_SuspendRendering(false);
	}
}
