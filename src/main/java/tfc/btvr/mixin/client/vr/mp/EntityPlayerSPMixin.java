package tfc.btvr.mixin.client.vr.mp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.player.Session;
import net.minecraft.core.world.World;
import org.lwjgl.util.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.btvr.itf.VRPlayerAttachments;
import tfc.btvr.lwjgl3.BTVRSetup;
import tfc.btvr.lwjgl3.MPManager;
import tfc.btvr.lwjgl3.generic.DeviceType;
import tfc.btvr.lwjgl3.openvr.SDevice;
import tfc.btvr.math.MatrixHelper;
import tfc.btvr.mp.packets.MatricesPacket;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin implements VRPlayerAttachments {
	@Inject(at = @At("TAIL"), method = "<init>")
	public void postInit(Minecraft minecraft, World world, Session session, int i, CallbackInfo ci) {
		MPManager.modPresent = true;
	}
	
	@Override
	public void better_than_vr$setEnabled(boolean value) {
	}
	
	@Override
	public boolean better_than_vr$enabled() {
		return BTVRSetup.checkVR();
	}
	
	@Override
	public Matrix4f better_than_vr$getMatrix(int device) {
		switch (device) {
			case 0:
				return MatrixHelper.toMat4(SDevice.getDeviceForRole(DeviceType.HEAD).getMatrix());
			case 1:
				return MatrixHelper.toMat4(SDevice.getDeviceForRole(DeviceType.LEFT_HAND).getMatrix());
			case 2:
				return MatrixHelper.toMat4(SDevice.getDeviceForRole(DeviceType.RIGHT_HAND).getMatrix());
			default:
				throw new RuntimeException("Invalid device " + device);
		}
	}
	
	@Override
	public void better_than_vr$handleMatricies(MatricesPacket packet) {
	}
}
