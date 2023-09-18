package tfc.btvr.itf;

import net.minecraft.core.HitResult;

public interface VRController {
	void better_than_vr$activateVRMining(HitResult result);
	HitResult better_than_vr$getResult();
	void better_than_vr$cancelMine();
	boolean better_than_vr$isMining();
}
