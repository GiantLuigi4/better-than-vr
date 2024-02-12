package tfc.btvr.mixin.client.vr.tweaks.mining;

import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.HitResult;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.util.helper.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.btvr.itf.VRController;

@Mixin(value = PlayerController.class, remap = false)
public class PlayerControllerMixin implements VRController {
	@Inject(at = @At("HEAD"), method = "stopDestroyBlock", cancellable = true)
	public void preCancelMining(boolean leftClickDown, CallbackInfo ci) {
		if (vrMiningTicks > 0) {
			vrMiningTicks--;
			ci.cancel();
		}
	}
	
	@Inject(at = @At("HEAD"), method = "destroyBlock")
	public void finishMining(int x, int y, int z, Side side, EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
		vrMiningTicks = 0;
		result = null;
	}
	
	@Unique
	int vrMiningTicks = 0;
	
	@Unique
	HitResult result;
	
	@Override
	public void better_than_vr$activateVRMining(HitResult result) {
		vrMiningTicks = 480;
		this.result = result;
	}
	
	@Override
	public HitResult better_than_vr$getResult() {
		if (vrMiningTicks <= 0) return null;
		return result;
	}
	
	@Override
	public void better_than_vr$cancelMine() {
		vrMiningTicks = 0;
		result = null;
	}
	
	@Override
	public boolean better_than_vr$isMining() {
		return vrMiningTicks > 0;
	}
	
	@Override
	public void better_than_vr$stopMining() {
		vrMiningTicks = 0;
	}
}
