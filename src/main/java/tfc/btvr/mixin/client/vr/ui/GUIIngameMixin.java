package tfc.btvr.mixin.client.vr.ui;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.option.ImmersiveModeOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfc.btvr.lwjgl3.BTVRSetup;

@Mixin(value = GuiIngame.class, remap = false)
public class GUIIngameMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/ImmersiveModeOption;drawCrosshair()Z"), method = "renderGameOverlay")
	public boolean redir(ImmersiveModeOption instance) {
		if (!BTVRSetup.checkVR()) return instance.drawCrosshair();
		
		return false;
	}
}
