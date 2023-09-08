package tfc.btvr.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.window.GameWindowLWJGL2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.BTVRSetup;

@Mixin(value = GameWindowLWJGL2.class, remap = false)
public class GameWindowMixin {
	@Inject(at = @At("HEAD"), method = "init")
	public void preInit(Minecraft minecraft, CallbackInfo ci) {
	}
	
	@Inject(at = @At("TAIL"), method = "init")
	public void postInit(Minecraft minecraft, CallbackInfo ci) {
		System.out.println(BTVRSetup.checkVR());
	}
}
