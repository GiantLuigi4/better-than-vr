package tfc.btvr.mixin.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.VRCamera;

@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {
	@Shadow private Minecraft mc;
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderGlobal;renderEntities(Lnet/minecraft/client/render/camera/ICamera;F)V"), method = "renderWorld")
	public void postRenderEntities(float renderPartialTicks, long updateRenderersUntil, CallbackInfo ci) {
		VRCamera.renderPlayer(this.mc.thePlayer, renderPartialTicks, mc.renderGlobal);
	}
}
