package tfc.btvr.mixin.client.vr.mp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.player.Session;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.lwjgl3.MPManager;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin {
	@Inject(at = @At("TAIL"), method = "<init>")
	public void postInit(Minecraft minecraft, World world, Session session, int i, CallbackInfo ci) {
		MPManager.modPresent = true;
	}
}
