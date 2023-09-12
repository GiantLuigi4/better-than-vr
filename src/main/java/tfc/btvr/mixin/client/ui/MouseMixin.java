package tfc.btvr.mixin.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.btvr.itf.VRScreenData;
import tfc.btvr.lwjgl3.VRManager;

@Mixin(value = Mouse.class, remap = false)
public class MouseMixin {
	@Inject(at = @At("HEAD"), method = "getX", cancellable = true)
	private static void preGetX(CallbackInfoReturnable<Integer> cir) {
		if (!VRManager.inStandby) {
			Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
			GuiScreen scrn = mc.currentScreen;
			if (scrn != null) {
				VRScreenData data = (VRScreenData) scrn;
				
				double d = data.better_than_vr$mouseOverride()[0];
				if (!Double.isNaN(d)) cir.setReturnValue((int) (d * mc.resolution.width));
				return;
			}
			cir.setReturnValue(-1);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "getY", cancellable = true)
	private static void preGetY(CallbackInfoReturnable<Integer> cir) {
		if (!VRManager.inStandby) {
			Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
			GuiScreen scrn = mc.currentScreen;
			if (scrn != null) {
				VRScreenData data = (VRScreenData) scrn;
				
				double d = data.better_than_vr$mouseOverride()[1];
				if (!Double.isNaN(d)) cir.setReturnValue((int) (d * mc.resolution.height));
				return;
			}
			cir.setReturnValue(-1);
		}
	}
}
